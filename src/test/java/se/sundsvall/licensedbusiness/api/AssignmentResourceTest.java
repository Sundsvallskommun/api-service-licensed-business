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
import se.sundsvall.licensedbusiness.Application;
import se.sundsvall.licensedbusiness.api.model.Assignment;
import se.sundsvall.licensedbusiness.api.model.AssignmentCreateRequest;
import se.sundsvall.licensedbusiness.api.model.AssignmentUpdateRequest;
import se.sundsvall.licensedbusiness.service.AssignmentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class AssignmentResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/{municipalityId}/assignments";
	private static final String ASSIGNMENT_PATH = "/{municipalityId}/assignments/{assignmentId}";
	private static final String ASSIGNMENT_ID = "123e4567-e89b-12d3-a456-426614174000";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private AssignmentService assignmentServiceMock;

	@Test
	void getAssignment() {
		final var expected = Assignment.create().withId(ASSIGNMENT_ID).withHolderName("Krogen AB").withValidFrom(LocalDate.of(2099, 1, 1)).withStatus("ACTIVE");
		when(assignmentServiceMock.getAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(ASSIGNMENT_PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "assignmentId", ASSIGNMENT_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Assignment.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(assignmentServiceMock).getAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID);
		verifyNoMoreInteractions(assignmentServiceMock);
	}

	@Test
	void createAssignment() {
		final var request = AssignmentCreateRequest.create()
			.withRestaurantNumberId("rn-1")
			.withAddressId("address-1")
			.withOrgNumber("556612-4144")
			.withHolderName("Restaurang i Sundsvall AB")
			.withPremisesName("Krogen")
			.withValidFrom(LocalDate.of(2099, 1, 1));
		when(assignmentServiceMock.createAssignment(MUNICIPALITY_ID, request)).thenReturn(ASSIGNMENT_ID);

		webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().location("/2281/assignments/" + ASSIGNMENT_ID)
			.expectBody().isEmpty();

		verify(assignmentServiceMock).createAssignment(MUNICIPALITY_ID, request);
		verifyNoMoreInteractions(assignmentServiceMock);
	}

	@Test
	void updateAssignment() {
		final var request = AssignmentUpdateRequest.create().withValidTo(LocalDate.of(2099, 12, 31)).withPremisesName("Nya krogen");
		final var expected = Assignment.create().withId(ASSIGNMENT_ID).withPremisesName("Nya krogen").withValidTo(LocalDate.of(2099, 12, 31));
		when(assignmentServiceMock.updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, request)).thenReturn(expected);

		final var response = webTestClient.patch()
			.uri(builder -> builder.path(ASSIGNMENT_PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "assignmentId", ASSIGNMENT_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Assignment.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(assignmentServiceMock).updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, request);
		verifyNoMoreInteractions(assignmentServiceMock);
	}
}
