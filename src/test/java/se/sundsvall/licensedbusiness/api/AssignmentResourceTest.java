package se.sundsvall.licensedbusiness.api;

import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.problem.Problem;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssignmentResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ASSIGNMENT_ID = "123e4567-e89b-12d3-a456-426614174000";

	private final AssignmentResource assignmentResource = new AssignmentResource();

	@Test
	void createAssignment() {
		assertThatThrownBy(() -> assignmentResource.createAssignment(MUNICIPALITY_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}

	@Test
	void updateAssignment() {
		assertThatThrownBy(() -> assignmentResource.updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}
}
