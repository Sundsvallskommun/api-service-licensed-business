package se.sundsvall.licensedbusiness.api;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.licensedbusiness.api.model.Assignment;
import se.sundsvall.licensedbusiness.api.model.AssignmentCreateRequest;
import se.sundsvall.licensedbusiness.api.model.AssignmentUpdateRequest;
import se.sundsvall.licensedbusiness.service.AssignmentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;

@ExtendWith(MockitoExtension.class)
class AssignmentResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ASSIGNMENT_ID = "123e4567-e89b-12d3-a456-426614174000";

	@Mock
	private AssignmentService assignmentService;

	@Test
	void getAssignment() {
		final var expected = Assignment.create().withId(ASSIGNMENT_ID);
		when(assignmentService.getAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID)).thenReturn(expected);

		final var assignmentResource = new AssignmentResource(assignmentService);
		final var response = assignmentResource.getAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(expected);
		verify(assignmentService).getAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID);
	}

	@Test
	void createAssignment() {
		final var request = AssignmentCreateRequest.create()
			.withRestaurantNumberId("rn-1")
			.withAddressId("address-1")
			.withOrgNumber("556612-4144")
			.withHolderName("Restaurang i Sundsvall AB")
			.withValidFrom(LocalDate.of(2026, 1, 1));
		when(assignmentService.createAssignment(MUNICIPALITY_ID, request)).thenReturn(ASSIGNMENT_ID);

		final var assignmentResource = new AssignmentResource(assignmentService);
		final var response = assignmentResource.createAssignment(MUNICIPALITY_ID, request);

		assertThat(response.getStatusCode()).isEqualTo(CREATED);
		assertThat(response.getHeaders().getLocation()).hasToString("/2281/assignments/" + ASSIGNMENT_ID);
		verify(assignmentService).createAssignment(MUNICIPALITY_ID, request);
	}

	@Test
	void updateAssignment() {
		final var request = AssignmentUpdateRequest.create().withValidTo(LocalDate.of(2026, 12, 31));
		final var expected = Assignment.create().withId(ASSIGNMENT_ID).withValidTo(LocalDate.of(2026, 12, 31));
		when(assignmentService.updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, request)).thenReturn(expected);

		final var assignmentResource = new AssignmentResource(assignmentService);
		final var response = assignmentResource.updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(expected);
		verify(assignmentService).updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, request);
	}
}
