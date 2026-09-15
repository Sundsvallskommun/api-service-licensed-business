package se.sundsvall.licensedbusiness.api;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.licensedbusiness.api.model.Assignment;
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.service.AssignmentService;
import se.sundsvall.licensedbusiness.service.RestaurantNumberService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;

@ExtendWith(MockitoExtension.class)
class RestaurantNumberResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String RESTAURANT_NUMBER = "22813670";

	@Mock
	private RestaurantNumberService restaurantNumberService;

	@Mock
	private AssignmentService assignmentService;

	@Test
	void getAvailableRestaurantNumbers() {
		final var expected = List.of(RestaurantNumber.create().withId("number-1").withNumber("1001").withMunicipalityId(MUNICIPALITY_ID));
		when(restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(expected);

		final var restaurantNumberResource = new RestaurantNumberResource(restaurantNumberService, assignmentService);
		final var response = restaurantNumberResource.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(expected);
		verify(restaurantNumberService).getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);
	}

	@Test
	void getRestaurantNumber() {
		final var expected = RestaurantNumber.create().withId("number-1").withNumber(RESTAURANT_NUMBER);
		when(restaurantNumberService.getRestaurantNumber(MUNICIPALITY_ID, RESTAURANT_NUMBER)).thenReturn(expected);

		final var restaurantNumberResource = new RestaurantNumberResource(restaurantNumberService, assignmentService);
		final var response = restaurantNumberResource.getRestaurantNumber(MUNICIPALITY_ID, RESTAURANT_NUMBER);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(expected);
		verify(restaurantNumberService).getRestaurantNumber(MUNICIPALITY_ID, RESTAURANT_NUMBER);
	}

	@Test
	void getLatestAssignment() {
		final var expected = Assignment.create().withId("assignment-1");
		when(assignmentService.getLatestAssignment(MUNICIPALITY_ID, RESTAURANT_NUMBER)).thenReturn(expected);

		final var restaurantNumberResource = new RestaurantNumberResource(restaurantNumberService, assignmentService);
		final var response = restaurantNumberResource.getLatestAssignment(MUNICIPALITY_ID, RESTAURANT_NUMBER);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(expected);
		verify(assignmentService).getLatestAssignment(MUNICIPALITY_ID, RESTAURANT_NUMBER);
	}

	@Test
	void createRestaurantNumber() {
		when(restaurantNumberService.createRestaurantNumber(MUNICIPALITY_ID)).thenReturn(RESTAURANT_NUMBER);

		final var restaurantNumberResource = new RestaurantNumberResource(restaurantNumberService, assignmentService);
		final var response = restaurantNumberResource.createRestaurantNumber(MUNICIPALITY_ID);

		assertThat(response.getStatusCode()).isEqualTo(CREATED);
		assertThat(response.getHeaders().getLocation()).hasToString("/2281/restaurant-numbers/" + RESTAURANT_NUMBER);
		verify(restaurantNumberService).createRestaurantNumber(MUNICIPALITY_ID);
	}
}
