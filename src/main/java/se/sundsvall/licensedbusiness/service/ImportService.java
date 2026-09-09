package se.sundsvall.licensedbusiness.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.csv.CSVFormat;
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

// Temporary one-off import path for seeding the register from the legacy excel export (converted to CSV
// externally). Expected header row: street_address,postal_code,postal_area,restaurant_number,org_number,
// holder_name,premises_name,valid_from,valid_to,status. Remove once the historical data has been imported.
// The whole file is imported in a single transaction: if any row fails, nothing is persisted.
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
				for (final var record : parser) {
					rowsProcessed++;
					try {
						final var streetAddress = record.get("street_address").trim();
						final var postalCode = record.get("postal_code").trim();
						final var postalArea = record.get("postal_area").trim();
						final var restaurantNumber = record.get("restaurant_number").trim();
						final var orgNumber = record.get("org_number").trim();
						final var holderName = record.get("holder_name").trim();
						final var premisesName = record.get("premises_name").trim();
						final var validFrom = LocalDate.parse(record.get("valid_from").trim());
						final var validToRaw = record.get("valid_to");
						final var validTo = (validToRaw == null || validToRaw.isBlank()) ? null : LocalDate.parse(validToRaw.trim());
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
							addError(errors, "Row %d: restaurant number '%s' is already tied to a different address, row skipped".formatted(record.getRecordNumber(), restaurantNumber));
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
						addError(errors, "Row %d: %s".formatted(record.getRecordNumber(), e.getMessage()));
					}
				}
			}
		} catch (final IOException e) {
			throw Problem.valueOf(BAD_REQUEST, "Could not read CSV file: " + e.getMessage());
		}

		if (!errors.isEmpty()) {
			throw Problem.valueOf(BAD_REQUEST, "Import failed, no data was saved: " + String.join("; ", errors));
		}

		return new ImportResult(rowsProcessed, addressesCreated, licenseHoldersCreated, restaurantNumbersCreated, assignmentsCreated, errors);
	}

	private static void addError(final List<String> errors, final String message) {
		if (errors.size() < MAX_ERRORS) {
			errors.add(message);
		} else if (errors.size() == MAX_ERRORS) {
			errors.add("... additional errors omitted");
		}
	}
}
