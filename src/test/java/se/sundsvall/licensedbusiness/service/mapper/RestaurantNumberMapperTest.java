package se.sundsvall.licensedbusiness.service.mapper;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
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
			.withCreated(created);

		final var restaurantNumber = restaurantNumberMapper.toRestaurantNumber(entity);

		assertThat(restaurantNumber.getId()).isEqualTo("number-1");
		assertThat(restaurantNumber.getNumber()).isEqualTo("1001");
		assertThat(restaurantNumber.getMunicipalityId()).isEqualTo("2281");
		assertThat(restaurantNumber.getCreated()).isEqualTo(created);
	}

	@Test
	void toRestaurantNumberWithNullEntity() {
		assertThat(restaurantNumberMapper.toRestaurantNumber(null)).isNull();
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
