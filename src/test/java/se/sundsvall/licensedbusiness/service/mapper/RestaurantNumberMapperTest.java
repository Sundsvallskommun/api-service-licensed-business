package se.sundsvall.licensedbusiness.service.mapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantNumberMapperTest {

	private final RestaurantNumberMapper restaurantNumberMapper = new RestaurantNumberMapper();

	@Test
	void toRestaurantNumber() {
		final var created = OffsetDateTime.now();
		final var entity = RestaurantNumberEntity.create()
			.withId("number-1")
			.withRestaurantNumber("1001")
			.withMunicipalityId("2281")
			.withReported(false)
			.withCreated(created);

		final var restaurantNumber = restaurantNumberMapper.toRestaurantNumber(entity);

		assertThat(restaurantNumber.getId()).isEqualTo("number-1");
		assertThat(restaurantNumber.getNumber()).isEqualTo("1001");
		assertThat(restaurantNumber.getMunicipalityId()).isEqualTo("2281");
		assertThat(restaurantNumber.getReported()).isFalse();
		assertThat(restaurantNumber.getCreated()).isEqualTo(created);
	}

	@Test
	void toRestaurantNumberWithNullEntity() {
		assertThat(restaurantNumberMapper.toRestaurantNumber(null)).isNull();
	}

	@Test
	void toAddressRestaurantNumber() {
		final var created = OffsetDateTime.now();
		final var entity = RestaurantNumberEntity.create()
			.withId("number-1")
			.withRestaurantNumber("22810001")
			.withMunicipalityId("2281")
			.withReported(true)
			.withCreated(created);
		final var latestAssignment = RestaurantNumberAssignmentEntity.create()
			.withPremisesName("Harrys Pub")
			.withValidFrom(LocalDate.of(2026, 1, 1))
			.withValidTo(LocalDate.of(2026, 12, 31));

		final var result = restaurantNumberMapper.toAddressRestaurantNumber(entity, latestAssignment, "ACTIVE");

		assertThat(result.getId()).isEqualTo("number-1");
		assertThat(result.getNumber()).isEqualTo("22810001");
		assertThat(result.getMunicipalityId()).isEqualTo("2281");
		assertThat(result.getReported()).isTrue();
		assertThat(result.getStatus()).isEqualTo("ACTIVE");
		assertThat(result.getPremisesName()).isEqualTo("Harrys Pub");
		assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
		assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
		assertThat(result.getCreated()).isEqualTo(created);
	}

	@Test
	void toAddressRestaurantNumberWithoutAssignment() {
		final var entity = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("22810001");

		final var result = restaurantNumberMapper.toAddressRestaurantNumber(entity, null, "AVAILABLE");

		assertThat(result.getId()).isEqualTo("number-1");
		assertThat(result.getStatus()).isEqualTo("AVAILABLE");
		assertThat(result.getPremisesName()).isNull();
		assertThat(result.getValidFrom()).isNull();
		assertThat(result.getValidTo()).isNull();
	}

	@Test
	void toRestaurantNumbers() {
		final var entity1 = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("1001");
		final var entity2 = RestaurantNumberEntity.create().withId("number-2").withRestaurantNumber("1002");

		final var restaurantNumbers = restaurantNumberMapper.toRestaurantNumbers(List.of(entity1, entity2));

		assertThat(restaurantNumbers).hasSize(2);
		assertThat(restaurantNumbers.get(0).getId()).isEqualTo("number-1");
		assertThat(restaurantNumbers.get(1).getId()).isEqualTo("number-2");
	}

	@Test
	void toRestaurantNumbersWithNullList() {
		assertThat(restaurantNumberMapper.toRestaurantNumbers(null)).isEmpty();
	}
}
