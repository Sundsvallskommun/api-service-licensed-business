package se.sundsvall.licensedbusiness.integration.db.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

/**
 * RestaurantNumberRepository tests.
 *
 * @see /src/test/resources/db/scripts/testdata.sql for data setup.
 */
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
class RestaurantNumberRepositoryTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private RestaurantNumberRepository restaurantNumberRepository;

	@Test
	void findAvailableByMunicipalityIdAndAddressIdReturnsRestaurantNumberWithoutActiveAssignment() {
		final var available = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-6");

		assertThat(available).extracting("id").containsExactlyInAnyOrder("rn-3", "rn-4");
	}

	@Test
	void findAvailableByMunicipalityIdAndAddressIdExcludesRestaurantNumberWithActiveAssignment() {
		final var available = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-1");

		assertThat(available).extracting("id").doesNotContain("rn-1", "rn-2");
	}

	@Test
	void findAvailableByMunicipalityIdAndAddressIdReturnsNeverAssignedRestaurantNumber() {
		final var available = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-1");

		assertThat(available).extracting("id").containsExactly("rn-5");
	}

	@Test
	void findAvailableByMunicipalityIdAndAddressIdIsScopedToTheAddressOfTheNumber() {
		final var atAddress1 = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-1");
		final var atAddress6 = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-6");

		assertThat(atAddress1).extracting("id").doesNotContain("rn-3", "rn-4");
		assertThat(atAddress6).extracting("id").doesNotContain("rn-5");
	}

	@Test
	void findAvailableByMunicipalityIdAndAddressIdWithNoMatch() {
		final var available = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-2");

		assertThat(available).isEmpty();
	}

	@Test
	void findByRestaurantNumberAndMunicipalityId() {
		final var restaurantNumber = restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId("2001", MUNICIPALITY_ID);

		assertThat(restaurantNumber).get().extracting("id").isEqualTo("rn-1");
	}

	@Test
	void findByRestaurantNumberAndMunicipalityIdIsScopedToMunicipality() {
		final var restaurantNumber = restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId("2001", "2260");

		assertThat(restaurantNumber).isEmpty();
	}

	@Test
	void findByIdAndMunicipalityId() {
		assertThat(restaurantNumberRepository.findByIdAndMunicipalityId("rn-1", MUNICIPALITY_ID)).isPresent();
		assertThat(restaurantNumberRepository.findByIdAndMunicipalityId("rn-1", "2260")).isEmpty();
	}

	@Test
	void findSequencedRestaurantNumbersSkipsNumbersOutsideTheGeneratedFormat() {
		final var numbers = restaurantNumberRepository.findSequencedRestaurantNumbers("2262");

		assertThat(numbers).containsExactlyInAnyOrder("22620001", "22620003", "2262037x");
	}

	@Test
	void findSequencedRestaurantNumbersWithNoMatch() {
		final var numbers = restaurantNumberRepository.findSequencedRestaurantNumbers(MUNICIPALITY_ID);

		assertThat(numbers).isEmpty();
	}
}
