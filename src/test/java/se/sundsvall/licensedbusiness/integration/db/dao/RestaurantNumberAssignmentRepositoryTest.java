package se.sundsvall.licensedbusiness.integration.db.dao;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
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

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private LicenseHolderRepository licenseHolderRepository;

	@Autowired
	private EntityManager entityManager;

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
	void findAllByRestaurantNumberAndStatusOrderByValidFromAscCreatedAsc() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-2").orElseThrow();

		final var active = restaurantNumberAssignmentRepository.findAllByRestaurantNumberAndStatusOrderByValidFromAscCreatedAsc(restaurantNumber, AssignmentStatus.ACTIVE);

		assertThat(active).extracting("id").containsExactly("assignment-4");
	}

	@Test
	void findAllByRestaurantNumberAndStatusWithNoActiveAssignment() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-3").orElseThrow();

		final var active = restaurantNumberAssignmentRepository.findAllByRestaurantNumberAndStatusOrderByValidFromAscCreatedAsc(restaurantNumber, AssignmentStatus.ACTIVE);

		assertThat(active).isEmpty();
	}

	@Test
	void findAllByRestaurantNumberAddressIdFollowsTheNumberNotTheAssignmentAddress() {
		final var assignments = restaurantNumberAssignmentRepository.findAllByRestaurantNumber_Address_Id("address-6");

		assertThat(assignments).extracting("id").containsExactlyInAnyOrder("assignment-5", "assignment-6", "assignment-7");
	}

	@Test
	void findAllByRestaurantNumberAndStatusOrdersByValidFromThenCreatedAndSkipsOtherStatuses() {
		final var restaurantNumber = restaurantNumberRepository.findById("rn-5").orElseThrow();
		final var address = addressRepository.findById("address-1").orElseThrow();
		final var licenseHolder = licenseHolderRepository.findById("holder-1").orElseThrow();
		final var laterStartCreatedSecond = persist(restaurantNumber, address, licenseHolder, "2024-01-01", AssignmentStatus.ACTIVE, "2024-01-02T10:00:00Z");
		final var earliestStart = persist(restaurantNumber, address, licenseHolder, "2023-01-01", AssignmentStatus.ACTIVE, "2024-01-03T10:00:00Z");
		final var laterStartCreatedFirst = persist(restaurantNumber, address, licenseHolder, "2024-01-01", AssignmentStatus.ACTIVE, "2024-01-01T10:00:00Z");
		persist(restaurantNumber, address, licenseHolder, "2022-01-01", AssignmentStatus.ENDED, "2022-01-01T10:00:00Z");
		entityManager.clear();

		final var active = restaurantNumberAssignmentRepository.findAllByRestaurantNumberAndStatusOrderByValidFromAscCreatedAsc(restaurantNumber, AssignmentStatus.ACTIVE);

		assertThat(active).extracting("id").containsExactly(earliestStart, laterStartCreatedFirst, laterStartCreatedSecond);
		assertThat(active.getFirst().getAddress().getStreetAddress()).isEqualTo("Storgatan 1");
		assertThat(active.getFirst().getLicenseHolder().getOrgNumber()).isEqualTo("5566112233");
	}

	private String persist(final RestaurantNumberEntity restaurantNumber, final AddressEntity address, final LicenseHolderEntity licenseHolder, final String validFrom,
		final AssignmentStatus status, final String created) {
		final var entity = restaurantNumberAssignmentRepository.saveAndFlush(RestaurantNumberAssignmentEntity.create()
			.withRestaurantNumber(restaurantNumber)
			.withAddress(address)
			.withLicenseHolder(licenseHolder)
			.withHolderName("Bolag A")
			.withValidFrom(LocalDate.parse(validFrom))
			.withStatus(status));
		entity.setCreated(OffsetDateTime.parse(created));
		return restaurantNumberAssignmentRepository.saveAndFlush(entity).getId();
	}

	@Test
	void updateBumpsVersionAndSetsModified() {
		final var assignment = restaurantNumberAssignmentRepository.findById("assignment-1").orElseThrow();
		final var versionBeforeUpdate = assignment.getVersion();
		assertThat(assignment.getModified()).isNull();

		assignment.setPremisesName("Ny lokal");
		restaurantNumberAssignmentRepository.saveAndFlush(assignment);

		assertThat(assignment.getVersion()).isEqualTo(versionBeforeUpdate + 1);
		assertThat(assignment.getModified()).isNotNull();
	}
}
