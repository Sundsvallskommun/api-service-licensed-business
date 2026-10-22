package se.sundsvall.licensedbusiness.api;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.licensedbusiness.Application;
import se.sundsvall.licensedbusiness.api.model.Assignment;
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.service.AssignmentService;
import se.sundsvall.licensedbusiness.service.RestaurantNumberService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class RestaurantNumberResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/{municipalityId}/restaurant-numbers";
	private static final String AVAILABLE_PATH = "/{municipalityId}/restaurant-numbers/available";
	private static final String RESTAURANT_NUMBER_PATH = "/{municipalityId}/restaurant-numbers/{restaurantNumber}";
	private static final String ASSIGNMENT_PATH = "/{municipalityId}/restaurant-numbers/{restaurantNumber}/assignment";
	private static final String ASSIGNMENTS_PATH = "/{municipalityId}/restaurant-numbers/{restaurantNumber}/assignments";
	private static final String ADDRESS_ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String RESTAURANT_NUMBER = "22813670";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private RestaurantNumberService restaurantNumberServiceMock;

	@MockitoBean
	private AssignmentService assignmentServiceMock;

	@Test
	void getAvailableRestaurantNumbers() {
		final var expected = List.of(RestaurantNumber.create().withId("number-1").withNumber("1001").withMunicipalityId(MUNICIPALITY_ID));
		when(restaurantNumberServiceMock.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(AVAILABLE_PATH).queryParam("addressId", ADDRESS_ID).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(RestaurantNumber.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(restaurantNumberServiceMock).getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);
		verifyNoMoreInteractions(restaurantNumberServiceMock);
		verifyNoInteractions(assignmentServiceMock);
	}

	@Test
	void getRestaurantNumber() {
		final var expected = RestaurantNumber.create().withId("number-1").withNumber(RESTAURANT_NUMBER);
		when(restaurantNumberServiceMock.getRestaurantNumber(MUNICIPALITY_ID, RESTAURANT_NUMBER)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(RESTAURANT_NUMBER_PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "restaurantNumber", RESTAURANT_NUMBER)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(RestaurantNumber.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(restaurantNumberServiceMock).getRestaurantNumber(MUNICIPALITY_ID, RESTAURANT_NUMBER);
		verifyNoMoreInteractions(restaurantNumberServiceMock);
		verifyNoInteractions(assignmentServiceMock);
	}

	@Test
	void getLatestAssignment() {
		final var expected = Assignment.create().withId("assignment-1");
		when(assignmentServiceMock.getLatestAssignment(MUNICIPALITY_ID, RESTAURANT_NUMBER)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(ASSIGNMENT_PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "restaurantNumber", RESTAURANT_NUMBER)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Assignment.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(assignmentServiceMock).getLatestAssignment(MUNICIPALITY_ID, RESTAURANT_NUMBER);
		verifyNoMoreInteractions(assignmentServiceMock);
		verifyNoInteractions(restaurantNumberServiceMock);
	}

	@Test
	void getActiveAssignments() {
		final var expected = List.of(Assignment.create().withId("assignment-1"), Assignment.create().withId("assignment-2"));
		when(assignmentServiceMock.getActiveAssignments(MUNICIPALITY_ID, RESTAURANT_NUMBER)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(ASSIGNMENTS_PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "restaurantNumber", RESTAURANT_NUMBER)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(Assignment.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(assignmentServiceMock).getActiveAssignments(MUNICIPALITY_ID, RESTAURANT_NUMBER);
		verifyNoMoreInteractions(assignmentServiceMock);
		verifyNoInteractions(restaurantNumberServiceMock);
	}

	@Test
	void createRestaurantNumber() {
		when(restaurantNumberServiceMock.createRestaurantNumber(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(RESTAURANT_NUMBER);

		webTestClient.post()
			.uri(builder -> builder.path(PATH).queryParam("addressId", ADDRESS_ID).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().location("/2281/restaurant-numbers/" + RESTAURANT_NUMBER)
			.expectBody().isEmpty();

		verify(restaurantNumberServiceMock).createRestaurantNumber(MUNICIPALITY_ID, ADDRESS_ID);
		verifyNoMoreInteractions(restaurantNumberServiceMock);
		verifyNoInteractions(assignmentServiceMock);
	}
}
