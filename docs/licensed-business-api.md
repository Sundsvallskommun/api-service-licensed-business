# Licensed business API

Tjänsten är ett register över restaurangnummer för serveringstillstånd. Den håller reda på vilket nummer som hör till vilken lokal och vilken tillståndshavare som har haft numret under vilken period. Den anropar inga andra tjänster. All data ligger i MariaDB och alla anrop går under `/{municipalityId}/...`.

## Datamodell

```
address ──< restaurant_number ──< restaurant_number_assignment >── license_holder
```

- `address` är gatuadress och postnummer, unik per kommun. Tjänsten normaliserar adressen innan den jämför, så `Storgatan 1 A` och `storgatan 1a` blir samma adress, liksom `852 30` och `85230`.
- `restaurant_number` hör till lokalen, inte till verksamheten. Numret ligger kvar på adressen när en krögare ersätts av nästa. Formatet är kommunkod plus fyra siffror, till exempel `22810001`, och ett nytt nummer får det lägsta lediga löpnumret i kommunen.
- `license_holder` identifieras på orgnummer, som normaliseras. Tjänsten skapar tillståndshavaren första gången orgnumret dyker upp.
- `restaurant_number_assignment` kopplar ihop nummer, adress och tillståndshavare under en period från `validFrom` till `validTo`. En öppen period saknar `validTo`. Status är `ACTIVE` eller `ENDED`.

## Endpoints

| Metod |                     Sökväg                     |                                 Vad                                 |
|-------|------------------------------------------------|---------------------------------------------------------------------|
| GET   | `/addresses`                                   | Paginerad lista                                                     |
| GET   | `/addresses/search?query=`                     | Fritextsökning på gatuadress                                        |
| GET   | `/addresses/lookup?streetAddress=&postalCode=` | Exakt träff på normaliserad adress                                  |
| GET   | `/addresses/{id}`                              | En adress                                                           |
| GET   | `/addresses/{id}/restaurant-numbers`           | Alla nummer på adressen med status och senaste tilldelning          |
| POST  | `/addresses`                                   | Skapa. Ger 409 med länk till den befintliga om adressen redan finns |
| POST  | `/restaurant-numbers?addressId=`               | Tilldela nästa lediga nummer till adressen                          |
| GET   | `/restaurant-numbers/{nr}`                     | Ett nummer                                                          |
| GET   | `/restaurant-numbers/available?addressId=`     | Nummer på adressen utan aktiv tilldelning                           |
| GET   | `/restaurant-numbers/{nr}/assignment`          | Senaste tilldelningen, med adress och tillståndshavare              |
| POST  | `/assignments`                                 | Skapa en tilldelning                                                |
| GET   | `/assignments/{id}`                            | En tilldelning                                                      |
| PATCH | `/assignments/{id}`                            | Ändra `validTo`, `premisesName` eller `holderName`                  |
| POST  | `/import/restaurant-numbers`                   | Engångsimport från CSV, tillfällig                                  |

Swagger UI finns på `/api-docs` och kontraktet i `src/integration-test/resources/api/openapi.yaml`.

## Affärsregler

- En ny tilldelning tar över numret. Om numret redan har en aktiv tilldelning och den nya perioden börjar senare, avslutar tjänsten den gamla dagen före den nyas `validFrom`. Andra överlappningar ger 400.
- Ett nummer kan bara tilldelas på den adress det hör till. Annars 400.
- Ett nummer har högst en aktiv tilldelning, även efter PATCH.
- Status räknas fram från `validTo`. Ett nattligt jobb klockan 01:00 sätter `ENDED` på tilldelningar vars `validTo` har passerat. Jobbet låses med shedlock så att bara en instans kör det.
- PATCH använder optimistisk låsning. Om två personer uppdaterar samma tilldelning samtidigt får den som kommer sist 409 och får ladda om.
- PATCH kan inte nollställa `validTo`. Fält som utelämnas lämnas orörda.

## Restaurangnummer på en adress

`GET /{municipalityId}/addresses/{addressId}/restaurant-numbers` listar alla nummer som hör till adressen, sorterade på nummer. Adressens id hittar man med `/addresses/search` eller `/addresses/lookup`. En okänd adress, eller en adress i en annan kommun, ger 404.

```json
[
  {
    "id": "it-number-1",
    "number": "22810001",
    "municipalityId": "2281",
    "status": "ACTIVE",
    "premisesName": "Provkrogen",
    "validFrom": "2020-01-01",
    "created": "2024-01-01T10:00:00+01:00"
  }
]
```

- `status` är `ACTIVE` om numret har en aktiv tilldelning, annars `AVAILABLE`. Samma regel avgör vad `/restaurant-numbers/available` returnerar.
- `premisesName`, `validFrom` och `validTo` visar serveringsstället som är öppet i dag. Tjänsten väljer den tilldelning som har börjat och har senast `validFrom`, och vid lika den senast skapade.
  - För ett ledigt nummer blir det det föregående serveringsstället.
  - Ett ägarbyte som börjar senare ändrar inte vad som visas förrän bytesdagen.
  - Om numret bara har tilldelningar som börjar senare visas den första av dem.
- Fälten saknas om numret aldrig har tilldelats. `validTo` saknas om perioden är öppen.

## Rapportering till Folkhälsomyndigheten

Verksamheten rapporterar nya restaurangnummer till Folkhälsomyndigheten. Kolumnen `restaurant_number.reported` visar om ett nummer är rapporterat, och fältet `reported` finns på `RestaurantNumber` i alla svar.

- Nummer som skapas via `POST /restaurant-numbers` får `reported = false`.
- Nummer som CSV-importen skapar får `reported = true`. Ett nummer som redan finns när importen körs behåller sitt värde.
- Nummer som fanns före migrationen `V1_1` fick `reported = true`.

API:et har ännu ingen endpoint för att lista orapporterade nummer eller markera ett nummer som rapporterat.

## Typiskt flöde för en ny verksamhet

1. Slå upp adressen med `GET /2281/addresses/lookup?streetAddress=Storgatan 1&postalCode=85230`. Vid 404, skapa den med `POST /2281/addresses` och ta id:t från `Location`-headern.
2. Hämta lediga nummer med `GET /2281/restaurant-numbers/available?addressId={id}`. Om listan är tom, skapa ett nytt nummer med `POST /2281/restaurant-numbers?addressId={id}`. Annars återanvänds lokalens nummer.
3. Skapa tilldelningen med `POST /2281/assignments` och skicka med `restaurantNumberId`, `addressId`, `orgNumber`, `holderName`, `premisesName` och `validFrom`.
4. När verksamheten upphör, sätt `validTo` med `PATCH /2281/assignments/{id}`.

Vid ägarbyte i samma lokal räcker steg 3 med den nya tillståndshavaren. Tjänsten avslutar den gamla tilldelningen.

## Import

`POST /{municipalityId}/import/restaurant-numbers` tar en CSV med kolumnerna `street_address, postal_code, postal_area, restaurant_number, org_number, holder_name, premises_name, valid_from, valid_to, status`.

Importen sparar allt eller ingenting. Ett enda fel på någon rad och inget sparas. Om samma nummer förekommer på flera adresser hamnar numret på adressen från raden med senast `valid_from`, och numret listas i `conflictingRestaurantNumbers` i svaret.

Endpointen finns bara för migreringen från det gamla Excel-registret och ska tas bort när den är klar. Den saknar därför OpenAPI-annotationer.

## Det som saknas

- Historik. Man kan bara hämta den senaste tilldelningen per nummer, inte alla.
- Sökning på orgnummer eller tillståndshavare. Det finns ingen endpoint för `LicenseHolder`.
- Listning av alla nummer i en kommun.
- Listning och markering av rapporterade nummer.
- DELETE-anrop och uppdatering av adresser.

