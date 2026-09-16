package se.sundsvall.licensedbusiness.service;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ACTIVE;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ENDED;
import static se.sundsvall.licensedbusiness.service.AssignmentStatusResolver.resolveStatus;

class AssignmentStatusResolverTest {

	@Test
	void openEndedAssignmentIsActive() {
		assertThat(resolveStatus(null)).isEqualTo(ACTIVE);
	}

	@Test
	void lastValidDayIsInclusive() {
		assertThat(resolveStatus(LocalDate.now())).isEqualTo(ACTIVE);
	}

	@Test
	void assignmentIsEndedTheDayAfterItsLastValidDay() {
		assertThat(resolveStatus(LocalDate.now().minusDays(1))).isEqualTo(ENDED);
	}

	@Test
	void futureLastValidDayIsActive() {
		assertThat(resolveStatus(LocalDate.now().plusYears(1))).isEqualTo(ACTIVE);
	}
}
