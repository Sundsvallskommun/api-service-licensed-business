package se.sundsvall.licensedbusiness.service.mapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentMapperTest {

	private final AssignmentMapper assignmentMapper = new AssignmentMapper(new RestaurantNumberMapper(), new AddressMapper(), new LicenseHolderMapper());

	@Test
	void toAssignment() {
		final var created = OffsetDateTime.now();
		final var entity = RestaurantNumberAssignmentEntity.create()
			.withId("assignment-1")
			.withRestaurantNumber(RestaurantNumberEntity.create().withId("rn-1").withRestaurantNumber("22813670").withMunicipalityId("2281"))
			.withAddress(AddressEntity.create().withId("address-1").withStreetAddress("Storgatan 1"))
			.withLicenseHolder(LicenseHolderEntity.create().withId("holder-1").withOrgNumber("556612-4144"))
			.withHolderName("Restaurang i Sundsvall AB")
			.withPremisesName("Harrys Pub")
			.withValidFrom(LocalDate.of(2024, 1, 1))
			.withValidTo(LocalDate.of(2024, 12, 31))
			.withStatus(AssignmentStatus.ACTIVE)
			.withCreated(created);

		final var assignment = assignmentMapper.toAssignment(entity);

		assertThat(assignment.getId()).isEqualTo("assignment-1");
		assertThat(assignment.getRestaurantNumber().getId()).isEqualTo("rn-1");
		assertThat(assignment.getRestaurantNumber().getNumber()).isEqualTo("22813670");
		assertThat(assignment.getAddress().getId()).isEqualTo("address-1");
		assertThat(assignment.getAddress().getStreetAddress()).isEqualTo("Storgatan 1");
		assertThat(assignment.getLicenseHolder().getId()).isEqualTo("holder-1");
		assertThat(assignment.getLicenseHolder().getOrgNumber()).isEqualTo("556612-4144");
		assertThat(assignment.getHolderName()).isEqualTo("Restaurang i Sundsvall AB");
		assertThat(assignment.getPremisesName()).isEqualTo("Harrys Pub");
		assertThat(assignment.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
		assertThat(assignment.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
		assertThat(assignment.getStatus()).isEqualTo("ACTIVE");
		assertThat(assignment.getCreated()).isEqualTo(created);
	}

	@Test
	void toAssignmentWithNullEntity() {
		assertThat(assignmentMapper.toAssignment(null)).isNull();
	}

	@Test
	void toAssignmentWithNullAssociations() {
		final var entity = RestaurantNumberAssignmentEntity.create().withId("assignment-1");

		final var assignment = assignmentMapper.toAssignment(entity);

		assertThat(assignment.getId()).isEqualTo("assignment-1");
		assertThat(assignment.getRestaurantNumber()).isNull();
		assertThat(assignment.getAddress()).isNull();
		assertThat(assignment.getLicenseHolder()).isNull();
	}
}
