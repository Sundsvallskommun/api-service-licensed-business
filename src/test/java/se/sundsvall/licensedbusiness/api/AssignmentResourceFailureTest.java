package se.sundsvall.licensedbusiness.api;

import java.time.LocalDate;
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
import se.sundsvall.licensedbusiness.api.model.AssignmentCreateRequest;
import se.sundsvall.licensedbusiness.api.model.AssignmentUpdateRequest;
import se.sundsvall.licensedbusiness.service.AssignmentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class AssignmentResourceFailureTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String INVALID_MUNICIPALITY_ID = "invalid";
	private static final String PATH = "/{municipalityId}/assignments";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private AssignmentService assignmentServiceMock;

	@Test
	void createAssignmentWithEmptyBody() {
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(AssignmentCreateRequest.create())
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
				tuple("restaurantNumberId", "must not be blank"),
				tuple("addressId", "must not be blank"),
				tuple("orgNumber", "must not be blank"),
				tuple("holderName", "must not be blank"),
				tuple("validFrom", "must not be null"));

		verifyNoInteractions(assignmentServiceMock);
	}

	@Test
	void createAssignmentWithInvalidMunicipalityId() {
		final var request = AssignmentCreateRequest.create()
			.withRestaurantNumberId("rn-1")
			.withAddressId("address-1")
			.withOrgNumber("556612-4144")
			.withHolderName("Restaurang i Sundsvall AB")
			.withValidFrom(LocalDate.of(2026, 1, 1));

		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", INVALID_MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("createAssignment.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(assignmentServiceMock);
	}

	@Test
	void updateAssignmentWithBlankHolderName() {
		final var response = webTestClient.patch()
			.uri(builder -> builder.path(PATH + "/{assignmentId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "assignmentId", "assignment-1")))
			.contentType(APPLICATION_JSON)
			.bodyValue(AssignmentUpdateRequest.create().withHolderName("   "))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("holderName", "must not be blank"));

		verifyNoInteractions(assignmentServiceMock);
	}

	@Test
	void getAssignmentWithInvalidMunicipalityId() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH + "/{assignmentId}").build(Map.of("municipalityId", INVALID_MUNICIPALITY_ID, "assignmentId", "assignment-1")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("getAssignment.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(assignmentServiceMock);
	}
}
