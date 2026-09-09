package se.sundsvall.licensedbusiness.api;

import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.problem.Problem;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestaurantNumberResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "123e4567-e89b-12d3-a456-426614174000";

	private final RestaurantNumberResource restaurantNumberResource = new RestaurantNumberResource();

	@Test
	void getInactiveRestaurantNumbers() {
		assertThatThrownBy(() -> restaurantNumberResource.getInactiveRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}

	@Test
	void createRestaurantNumber() {
		assertThatThrownBy(() -> restaurantNumberResource.createRestaurantNumber(MUNICIPALITY_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}
}
