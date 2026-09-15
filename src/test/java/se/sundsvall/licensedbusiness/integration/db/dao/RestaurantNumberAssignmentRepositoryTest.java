package se.sundsvall.licensedbusiness.integration.db.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

/**
 * RestaurantNumberAssignmentRepository tests.
 *
 * @see /src/test/resources/db/scripts/testdata.sql for data setup.
 */
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
class RestaurantNumberAssignmentRepositoryTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	@Autowired
	private RestaurantNumberRepository restaurantNumberRepository;

	@Test
	void findFirstByRestaurantNumberReturnsTheAssignmentWithTheHighestValidFrom() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-1").orElseThrow();

		final var latest = restaurantNumberAssignmentRepository.findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(restaurantNumber);

		assertThat(latest).get().extracting("id").isEqualTo("assignment-2");
	}

	@Test
	void findFirstByRestaurantNumberReturnsAnActiveLatestAssignment() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-2").orElseThrow();

		final var latest = restaurantNumberAssignmentRepository.findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(restaurantNumber);

		assertThat(latest).get().extracting("id").isEqualTo("assignment-4");
	}

	@Test
	void findFirstByRestaurantNumberFollowsTheNumberToItsLatestAddress() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-3").orElseThrow();

		final var latest = restaurantNumberAssignmentRepository.findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(restaurantNumber);

		assertThat(latest).get().extracting("id").isEqualTo("assignment-6");
		assertThat(latest.orElseThrow().getAddress().getId()).isEqualTo("address-6");
	}

	@Test
	void findFirstByRestaurantNumberFetchesTheAssociationsEagerly() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-1").orElseThrow();

		final var latest = restaurantNumberAssignmentRepository.findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(restaurantNumber).orElseThrow();

		assertThat(latest.getRestaurantNumber().getRestaurantNumber()).isEqualTo("2001");
		assertThat(latest.getAddress().getStreetAddress()).isEqualTo("Storgatan 1");
		assertThat(latest.getLicenseHolder().getOrgNumber()).isEqualTo("5566112233");
	}

	@Test
	void findFirstByRestaurantNumberWithoutAssignment() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-5").orElseThrow();

		final var latest = restaurantNumberAssignmentRepository.findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(restaurantNumber);

		assertThat(latest).isEmpty();
	}

	@Test
	void findByIdAndMunicipalityId() {
		final var assignment = restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId("assignment-1", MUNICIPALITY_ID);

		assertThat(assignment).get().extracting("id").isEqualTo("assignment-1");
	}

	@Test
	void findByIdAndMunicipalityIdIsScopedToMunicipality() {
		final var assignment = restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId("assignment-1", "2260");

		assertThat(assignment).isEmpty();
	}

	@Test
	void findAllByRestaurantNumberAndStatus() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-2").orElseThrow();

		final var active = restaurantNumberAssignmentRepository.findAllByRestaurantNumberAndStatus(restaurantNumber, AssignmentStatus.ACTIVE);

		assertThat(active).extracting("id").containsExactly("assignment-4");
	}

	@Test
	void findAllByRestaurantNumberReturnsEndedAssignmentsToo() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-1").orElseThrow();

		final var all = restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber);

		assertThat(all).extracting("id").containsExactlyInAnyOrder("assignment-1", "assignment-2");
	}

	@Test
	void findAllByRestaurantNumberWithoutAssignment() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-5").orElseThrow();

		assertThat(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).isEmpty();
	}

	@Test
	void findAllByRestaurantNumberAndStatusWithNoActiveAssignment() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-3").orElseThrow();

		final var active = restaurantNumberAssignmentRepository.findAllByRestaurantNumberAndStatus(restaurantNumber, AssignmentStatus.ACTIVE);

		assertThat(active).isEmpty();
	}
}
