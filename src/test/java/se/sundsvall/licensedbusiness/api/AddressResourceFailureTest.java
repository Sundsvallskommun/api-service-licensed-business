package se.sundsvall.licensedbusiness.api;

import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ProblemResponse;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;
import se.sundsvall.licensedbusiness.Application;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.service.AddressService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class AddressResourceFailureTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String INVALID_MUNICIPALITY_ID = "invalid";
	private static final String PATH = "/{municipalityId}/addresses";
	private static final String SEARCH_PATH = "/{municipalityId}/addresses/search";
	private static final String LOOKUP_PATH = "/{municipalityId}/addresses/lookup";
	private static final String ADDRESS_ID = "9ce333ec-a473-438b-8406-a71e957dc107";

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
	void lookupAddressWithoutParameters() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(LOOKUP_PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
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

	@Test
	void lookupAddressWithBlankPostalCode() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(LOOKUP_PATH).queryParam("streetAddress", "Storgatan 1").queryParam("postalCode", " ").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("postalCode", "must not be blank"));

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void lookupAddressWithInvalidMunicipalityId() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(LOOKUP_PATH).queryParam("streetAddress", "Storgatan 1").queryParam("postalCode", "852 30").build(Map.of("municipalityId", INVALID_MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("lookupAddress.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void lookupAddressNotFoundIsAProblemResponse() {
		when(addressServiceMock.lookupAddress(eq(MUNICIPALITY_ID), any()))
			.thenThrow(Problem.valueOf(NOT_FOUND, "Address Storgatan 1, 852 30 not found"));

		final var response = webTestClient.get()
			.uri(builder -> builder.path(LOOKUP_PATH).queryParam("streetAddress", "Storgatan 1").queryParam("postalCode", "852 30").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ProblemResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(response.getDetail()).isEqualTo("Address Storgatan 1, 852 30 not found");
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

	@Test
	void createAddressConflictCarriesTheExistingAddressId() {
		when(addressServiceMock.createAddress(eq(MUNICIPALITY_ID), any()))
			.thenThrow(Problem.builder()
				.withStatus(CONFLICT)
				.withTitle(CONFLICT.getReasonPhrase())
				.withDetail("Address Storgatan 1, 852 30 already exists with ID " + ADDRESS_ID)
				.withInstance(URI.create("/2281/addresses/" + ADDRESS_ID))
				.build());

		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(Address.create().withStreetAddress("Storgatan 1").withPostalCode("852 30"))
			.exchange()
			.expectStatus().isEqualTo(CONFLICT)
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ProblemResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getDetail()).contains(ADDRESS_ID);
		assertThat(response.getInstance()).isEqualTo(URI.create("/2281/addresses/" + ADDRESS_ID));
	}
}
