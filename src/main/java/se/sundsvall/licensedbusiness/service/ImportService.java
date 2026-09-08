package se.sundsvall.licensedbusiness.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Service;
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

// Temporary one-off import path for seeding the register from the legacy excel export (converted to CSV
// externally). Expected header row: street_address,postal_code,postal_area,restaurant_number,org_number,
// holder_name,premises_name,valid_from,valid_to,status. Remove once the historical data has been imported.
@Service
public class ImportService {

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

	public ImportResult importRestaurantNumbers(final String municipalityId, final MultipartFile file) {
		var rowsProcessed = 0;
		var addressesCreated = 0;
		var licenseHoldersCreated = 0;
		var restaurantNumbersCreated = 0;
		var assignmentsCreated = 0;
		final List<String> errors = new ArrayList<>();

		final var format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
			.setHeader()
			.setSkipHeaderRecord(true)
			.build();

		try (var reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
			var parser = format.parse(reader)) {

			for (final var record : parser) {
				rowsProcessed++;
				try {
					final var streetAddress = record.get("street_address");
					final var postalCode = record.get("postal_code");
					final var postalArea = record.get("postal_area");
					final var restaurantNumber = record.get("restaurant_number");
					final var orgNumber = record.get("org_number");
					final var holderName = record.get("holder_name");
					final var premisesName = record.get("premises_name");
					final var validFrom = LocalDate.parse(record.get("valid_from"));
					final var validToRaw = record.get("valid_to");
					final var validTo = (validToRaw == null || validToRaw.isBlank()) ? null : LocalDate.parse(validToRaw);
					final var status = AssignmentStatus.valueOf(record.get("status").trim().toUpperCase(Locale.ROOT));

					var address = addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId(streetAddress, postalCode, municipalityId).orElse(null);
					if (address == null) {
						address = addressRepository.save(AddressEntity.create()
							.withStreetAddress(streetAddress)
							.withPostalCode(postalCode)
							.withPostalArea(postalArea)
							.withMunicipalityId(municipalityId));
						addressesCreated++;
					}

					var licenseHolder = licenseHolderRepository.findByOrgNumber(orgNumber).orElse(null);
					if (licenseHolder == null) {
						licenseHolder = licenseHolderRepository.save(LicenseHolderEntity.create()
							.withOrgNumber(orgNumber)
							.withName(holderName));
						licenseHoldersCreated++;
					}

					var restaurantNumberEntity = restaurantNumberRepository.findByRestaurantNumber(restaurantNumber).orElse(null);
					if (restaurantNumberEntity == null) {
						restaurantNumberEntity = restaurantNumberRepository.save(RestaurantNumberEntity.create()
							.withRestaurantNumber(restaurantNumber)
							.withMunicipalityId(municipalityId)
							.withAddress(address));
						restaurantNumbersCreated++;
					} else if (!restaurantNumberEntity.getAddress().getId().equals(address.getId())) {
						errors.add("Row %d: restaurant number '%s' is already tied to a different address, row skipped".formatted(record.getRecordNumber(), restaurantNumber));
						continue;
					}

					restaurantNumberAssignmentRepository.save(RestaurantNumberAssignmentEntity.create()
						.withRestaurantNumber(restaurantNumberEntity)
						.withLicenseHolder(licenseHolder)
						.withHolderName(holderName)
						.withPremisesName(premisesName)
						.withValidFrom(validFrom)
						.withValidTo(validTo)
						.withStatus(status));
					assignmentsCreated++;
				} catch (final Exception e) {
					errors.add("Row %d: %s".formatted(record.getRecordNumber(), e.getMessage()));
				}
			}
		} catch (final IOException e) {
			throw Problem.valueOf(BAD_REQUEST, "Could not read CSV file: " + e.getMessage());
		}

		return new ImportResult(rowsProcessed, addressesCreated, licenseHoldersCreated, restaurantNumbersCreated, assignmentsCreated, errors);
	}
}
