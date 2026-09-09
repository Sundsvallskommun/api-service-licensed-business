package se.sundsvall.licensedbusiness.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.LicenseHolderRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberAssignmentRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private LicenseHolderRepository licenseHolderRepository;

	@Mock
	private RestaurantNumberRepository restaurantNumberRepository;

	@Mock
	private RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	@Test
	void importRestaurantNumbers() {
		final var savedStorgatan = AddressEntity.create().withId("address-1").withStreetAddress("Storgatan 1").withPostalCode("852 30");
		final var savedHolderA = LicenseHolderEntity.create().withId("holder-1").withOrgNumber("5566112233");
		final var savedHolderB = LicenseHolderEntity.create().withId("holder-2").withOrgNumber("5566998877");
		final var savedRestaurantNumber = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("1001").withAddress(savedStorgatan);

		when(addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId("Storgatan 1", "852 30", MUNICIPALITY_ID))
			.thenReturn(Optional.empty(), Optional.of(savedStorgatan));
		when(addressRepository.save(any())).thenReturn(savedStorgatan);

		when(licenseHolderRepository.findByOrgNumber("5566112233")).thenReturn(Optional.empty());
		when(licenseHolderRepository.findByOrgNumber("5566998877")).thenReturn(Optional.empty());
		when(licenseHolderRepository.save(any())).thenReturn(savedHolderA, savedHolderB);

		when(restaurantNumberRepository.findByRestaurantNumber("1001")).thenReturn(Optional.empty(), Optional.of(savedRestaurantNumber));
		when(restaurantNumberRepository.save(any())).thenReturn(savedRestaurantNumber);

		// Leading/trailing whitespace on the first row verifies that CSV fields are trimmed before use.
		final var csv = """
			street_address,postal_code,postal_area,restaurant_number,org_number,holder_name,premises_name,valid_from,valid_to,status
			 Storgatan 1 , 852 30 ,Sundsvall,1001, 5566112233 ,Bolag A,Pub A,2020-01-01,2021-01-01,ENDED
			Storgatan 1,852 30,Sundsvall,1001,5566998877,Bolag B,Pub B,2021-01-01,,ACTIVE
			""";
		final var file = new MockMultipartFile("file", "import.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

		final var importService = new ImportService(addressRepository, licenseHolderRepository, restaurantNumberRepository, restaurantNumberAssignmentRepository);
		final var result = importService.importRestaurantNumbers(MUNICIPALITY_ID, file);

		assertThat(result.rowsProcessed()).isEqualTo(2);
		assertThat(result.addressesCreated()).isEqualTo(1);
		assertThat(result.licenseHoldersCreated()).isEqualTo(2);
		assertThat(result.restaurantNumbersCreated()).isEqualTo(1);
		assertThat(result.assignmentsCreated()).isEqualTo(2);
		assertThat(result.errors()).isEmpty();
		verify(addressRepository, times(2)).findByStreetAddressAndPostalCodeAndMunicipalityId("Storgatan 1", "852 30", MUNICIPALITY_ID);
	}

	@Test
	void importRestaurantNumbersRollsBackOnRowError() {
		final var csv = """
			street_address,postal_code,postal_area,restaurant_number,org_number,holder_name,premises_name,valid_from,valid_to,status
			Storgatan 1,852 30,Sundsvall,1001,5566112233,Bolag A,Pub A,2020-01-01,2021-01-01,ENDED
			Storgatan 2,852 31,Sundsvall,1002,5566112233,Bolag A,Pub C,not-a-date,,ACTIVE
			""";
		final var file = new MockMultipartFile("file", "import.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

		final var importService = new ImportService(addressRepository, licenseHolderRepository, restaurantNumberRepository, restaurantNumberAssignmentRepository);

		assertThatThrownBy(() -> importService.importRestaurantNumbers(MUNICIPALITY_ID, file))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Import failed, no data was saved")
			.hasMessageContaining("Row 2");
	}

	@Test
	void importRestaurantNumbersStripsUtf8Bom() {
		final var savedStorgatan = AddressEntity.create().withId("address-1").withStreetAddress("Storgatan 1").withPostalCode("852 30");
		final var savedHolder = LicenseHolderEntity.create().withId("holder-1").withOrgNumber("5566112233");
		final var savedRestaurantNumber = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("1001").withAddress(savedStorgatan);

		when(addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId("Storgatan 1", "852 30", MUNICIPALITY_ID)).thenReturn(Optional.empty());
		when(addressRepository.save(any())).thenReturn(savedStorgatan);
		when(licenseHolderRepository.findByOrgNumber("5566112233")).thenReturn(Optional.empty());
		when(licenseHolderRepository.save(any())).thenReturn(savedHolder);
		when(restaurantNumberRepository.findByRestaurantNumber("1001")).thenReturn(Optional.empty());
		when(restaurantNumberRepository.save(any())).thenReturn(savedRestaurantNumber);

		final var csv = "﻿street_address,postal_code,postal_area,restaurant_number,org_number,holder_name,premises_name,valid_from,valid_to,status\n"
			+ "Storgatan 1,852 30,Sundsvall,1001,5566112233,Bolag A,Pub A,2020-01-01,2021-01-01,ENDED\n";
		final var file = new MockMultipartFile("file", "import.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

		final var importService = new ImportService(addressRepository, licenseHolderRepository, restaurantNumberRepository, restaurantNumberAssignmentRepository);
		final var result = importService.importRestaurantNumbers(MUNICIPALITY_ID, file);

		assertThat(result.errors()).isEmpty();
		assertThat(result.assignmentsCreated()).isEqualTo(1);
	}

	@Test
	void importRestaurantNumbersWithUnreadableFile() {
		final var file = new MockMultipartFile("file", "import.csv", "text/csv", new byte[0]) {
			@Override
			public InputStream getInputStream() throws IOException {
				throw new IOException("broken stream");
			}
		};

		final var importService = new ImportService(addressRepository, licenseHolderRepository, restaurantNumberRepository, restaurantNumberAssignmentRepository);

		assertThatThrownBy(() -> importService.importRestaurantNumbers(MUNICIPALITY_ID, file))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Could not read CSV file");
	}
}
