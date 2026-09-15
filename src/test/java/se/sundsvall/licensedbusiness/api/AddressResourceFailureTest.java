package se.sundsvall.licensedbusiness.api;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;
import se.sundsvall.licensedbusiness.Application;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.service.AddressService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class AddressResourceFailureTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String INVALID_MUNICIPALITY_ID = "invalid";
	private static final String PATH = "/{municipalityId}/addresses";
	private static final String SEARCH_PATH = "/{municipalityId}/addresses/search";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private AddressService addressServiceMock;

	@Test
	void getAddressesWithInvalidMunicipalityId() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", INVALID_MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("getAddresses.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void getAddressesWithPageZero() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).queryParam("page", 0).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("page", "must be greater than or equal to 1"));

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void getAddressesWithLimitZero() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).queryParam("limit", 0).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("limit", "must be greater than or equal to 1"));

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void getAddressesWithInvalidSortBy() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).queryParam("sortBy", "unknownProperty").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).singleElement().satisfies(violation -> {
			assertThat(violation.field()).isEqualTo("addressPagingParameters");
			assertThat(violation.message()).startsWith("One or more of the sortBy properties [unknownProperty] are not valid.");
		});

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void searchAddressesWithInvalidSortBy() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH).queryParam("query", "Storgatan").queryParam("sortBy", "unknownProperty").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).singleElement().satisfies(violation -> {
			assertThat(violation.field()).isEqualTo("addressSearchParameters");
			assertThat(violation.message()).startsWith("One or more of the sortBy properties [unknownProperty] are not valid.");
		});

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void searchAddressesWithoutQuery() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("query", "must not be blank"));

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void searchAddressesWithEmptyQuery() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH).queryParam("query", "").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("query", "must not be blank"));

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void searchAddressesWithBlankQuery() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH).queryParam("query", "   ").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("query", "must not be blank"));

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void searchAddressesWithPageZero() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH).queryParam("query", "Storgatan").queryParam("page", 0).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("page", "must be greater than or equal to 1"));

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void createAddressWithBlankStreetAddressAndPostalCode() {
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(Address.create().withStreetAddress(" ").withPostalCode(""))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(
				tuple("streetAddress", "must not be blank"),
				tuple("postalCode", "must not be blank"));

		verifyNoInteractions(addressServiceMock);
	}
}
