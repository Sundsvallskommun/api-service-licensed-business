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
	void findAvailableByMunicipalityIdAndAddressIdReturnsRestaurantNumberWhoseLatestAssignmentIsEnded() {
		final var available = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-1");

		assertThat(available).extracting("id").contains("rn-1");
	}

	@Test
	void findAvailableByMunicipalityIdAndAddressIdExcludesRestaurantNumberWhoseLatestAssignmentIsActive() {
		final var available = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-1");

		assertThat(available).extracting("id").isNotEmpty().doesNotContain("rn-2");
	}

	@Test
	void findAvailableByMunicipalityIdAndAddressIdIncludesRestaurantNumberWithNoAssignment() {
		final var available = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-1");

		assertThat(available).extracting("id").contains("rn-3");
	}

	@Test
	void findAvailableByMunicipalityIdAndAddressIdIsScopedToAddress() {
		final var available = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-6");

		assertThat(available).extracting("id").containsExactly("rn-4");
	}

	@Test
	void findAvailableByMunicipalityIdAndAddressIdWithNoMatch() {
		final var available = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, "address-2");

		assertThat(available).isEmpty();
	}
}
