package se.sundsvall.licensedbusiness.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.ImportResult;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.LicenseHolderRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberAssignmentRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static se.sundsvall.licensedbusiness.service.AddressNormalizer.normalizePostalCode;
import static se.sundsvall.licensedbusiness.service.AddressNormalizer.normalizeStreetAddress;
import static se.sundsvall.licensedbusiness.service.TextSanitizer.sanitize;

@Service
public class ImportService {

	private static final int MAX_ERRORS = 100;
	private static final int BOM_CHAR = 0xFEFF;

	private final AddressRepository addressRepository;

	private final LicenseHolderRepository licenseHolderRepository;

	private final RestaurantNumberRepository restaurantNumberRepository;

	private final RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	public ImportService(final AddressRepository addressRepository, final LicenseHolderRepository licenseHolderRepository, final RestaurantNumberRepository restaurantNumberRepository,
		final RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository) {
		this.addressRepository = addressRepository;
		this.licenseHolderRepository = licenseHolderRepository;
		this.restaurantNumberRepository = restaurantNumberRepository;
		this.restaurantNumberAssignmentRepository = restaurantNumberAssignmentRepository;
	}

	@Transactional
	public ImportResult importRestaurantNumbers(final String municipalityId, final MultipartFile file) {
		var rowsProcessed = 0;
		var addressesCreated = 0;
		var licenseHoldersCreated = 0;
		var restaurantNumbersCreated = 0;
		var assignmentsCreated = 0;
		final List<String> errors = new ArrayList<>();
		final Set<String> conflictingRestaurantNumbers = new TreeSet<>();
		final Map<String, LocalDate> addressValidFromPerNumber = new HashMap<>();

		final var format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
			.setHeader()
			.setSkipHeaderRecord(true)
			.build();

		try (var input = file.getInputStream();
			var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {

			reader.mark(1);
			if (reader.read() != BOM_CHAR) {
				reader.reset();
			}

			try (var parser = format.parse(reader)) {
				for (final var csvRecord : parser) {
					rowsProcessed++;
					try {
						final var created = importRow(municipalityId, csvRecord, conflictingRestaurantNumbers, addressValidFromPerNumber);
						addressesCreated += created.addresses();
						licenseHoldersCreated += created.licenseHolders();
						restaurantNumbersCreated += created.restaurantNumbers();
						assignmentsCreated++;
					} catch (final Exception e) {
						addError(errors, "Row %d: %s".formatted(csvRecord.getRecordNumber(), sanitize(e.getMessage())));
					}
				}
			}
		} catch (final IOException e) {
			throw Problem.valueOf(BAD_REQUEST, "Could not read CSV file: %s".formatted(sanitize(e.getMessage())));
		}

		if (!errors.isEmpty()) {
			throw Problem.valueOf(BAD_REQUEST, "Import failed, no data was saved: %s".formatted(String.join("; ", errors)));
		}

		return new ImportResult(rowsProcessed, addressesCreated, licenseHoldersCreated, restaurantNumbersCreated, assignmentsCreated, errors, List.copyOf(conflictingRestaurantNumbers));
	}

	private CreatedCounts importRow(final String municipalityId, final CSVRecord csvRecord, final Set<String> conflictingRestaurantNumbers, final Map<String, LocalDate> addressValidFromPerNumber) {
		final var streetAddress = normalizeStreetAddress(csvRecord.get("street_address"));
		final var postalCode = normalizePostalCode(csvRecord.get("postal_code"));
		final var postalArea = csvRecord.get("postal_area").trim();
		final var restaurantNumber = csvRecord.get("restaurant_number").trim();
		final var orgNumber = OrgNumberNormalizer.normalize(csvRecord.get("org_number"));
		final var holderName = csvRecord.get("holder_name").trim();
		final var premisesName = csvRecord.get("premises_name").trim();
		final var validFrom = LocalDate.parse(csvRecord.get("valid_from").trim());
		final var validToRaw = csvRecord.get("valid_to");
		final var validTo = (validToRaw == null || validToRaw.isBlank()) ? null : LocalDate.parse(validToRaw.trim());
		final var status = AssignmentStatus.valueOf(csvRecord.get("status").trim().toUpperCase(Locale.ROOT));

		var addressesCreated = 0;
		var address = addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId(streetAddress, postalCode, municipalityId).orElse(null);
		if (address == null) {
			address = addressRepository.save(AddressEntity.create()
				.withStreetAddress(streetAddress)
				.withPostalCode(postalCode)
				.withPostalArea(postalArea)
				.withMunicipalityId(municipalityId));
			addressesCreated = 1;
		}

		var licenseHoldersCreated = 0;
		var licenseHolder = licenseHolderRepository.findByOrgNumber(orgNumber).orElse(null);
		if (licenseHolder == null) {
			licenseHolder = licenseHolderRepository.save(LicenseHolderEntity.create()
				.withOrgNumber(orgNumber)
				.withName(holderName));
			licenseHoldersCreated = 1;
		}

		var restaurantNumbersCreated = 0;
		var restaurantNumberEntity = restaurantNumberRepository.findByRestaurantNumber(restaurantNumber).orElse(null);
		if (restaurantNumberEntity == null) {
			restaurantNumberEntity = restaurantNumberRepository.save(RestaurantNumberEntity.create()
				.withRestaurantNumber(restaurantNumber)
				.withMunicipalityId(municipalityId)
				.withAddress(address));
			restaurantNumbersCreated = 1;
			addressValidFromPerNumber.put(restaurantNumber, validFrom);
		} else {
			resolveAddress(restaurantNumberEntity, address, validFrom, conflictingRestaurantNumbers, addressValidFromPerNumber);
		}

		restaurantNumberAssignmentRepository.save(RestaurantNumberAssignmentEntity.create()
			.withRestaurantNumber(restaurantNumberEntity)
			.withLicenseHolder(licenseHolder)
			.withAddress(address)
			.withHolderName(holderName)
			.withPremisesName(premisesName)
			.withValidFrom(validFrom)
			.withValidTo(validTo)
			.withStatus(status));

		return new CreatedCounts(addressesCreated, licenseHoldersCreated, restaurantNumbersCreated);
	}

	// The register holds numbers that appear at more than one address. The number belongs to one address, so
	// the most recent assignment decides which, and the case is reported back to the caller. Every row is
	// weighed in, including one that matches the address already held, so that a later but older row cannot
	// take the number over.
	private static void resolveAddress(final RestaurantNumberEntity restaurantNumber, final AddressEntity address, final LocalDate validFrom,
		final Set<String> conflictingRestaurantNumbers, final Map<String, LocalDate> addressValidFromPerNumber) {
		if (!restaurantNumber.getAddress().getId().equals(address.getId())) {
			conflictingRestaurantNumbers.add(restaurantNumber.getRestaurantNumber());
		}

		if (validFrom.isAfter(addressValidFromPerNumber.getOrDefault(restaurantNumber.getRestaurantNumber(), LocalDate.MIN))) {
			restaurantNumber.setAddress(address);
			addressValidFromPerNumber.put(restaurantNumber.getRestaurantNumber(), validFrom);
		}
	}

	private record CreatedCounts(int addresses, int licenseHolders, int restaurantNumbers) {}

	private static void addError(final List<String> errors, final String message) {
		if (errors.size() < MAX_ERRORS) {
			errors.add(message);
		} else if (errors.size() == MAX_ERRORS) {
			errors.add("... additional errors omitted");
		}
	}
}
