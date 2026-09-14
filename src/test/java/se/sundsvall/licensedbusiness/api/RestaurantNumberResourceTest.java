package se.sundsvall.licensedbusiness.api;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.service.RestaurantNumberService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantNumberResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "123e4567-e89b-12d3-a456-426614174000";

	@Mock
	private RestaurantNumberService restaurantNumberService;

	@Test
	void getAvailableRestaurantNumbers() {
		final var expected = List.of(RestaurantNumber.create().withId("number-1").withNumber("1001").withMunicipalityId(MUNICIPALITY_ID));
		when(restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(expected);

		final var restaurantNumberResource = new RestaurantNumberResource(restaurantNumberService);
		final var response = restaurantNumberResource.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(expected);
		verify(restaurantNumberService).getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);
	}

	@Test
	void createRestaurantNumber() {
		final var restaurantNumberResource = new RestaurantNumberResource(restaurantNumberService);

		assertThatThrownBy(() -> restaurantNumberResource.createRestaurantNumber(MUNICIPALITY_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}
}
