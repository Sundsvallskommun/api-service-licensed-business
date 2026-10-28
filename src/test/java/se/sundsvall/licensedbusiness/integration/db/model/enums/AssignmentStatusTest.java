package se.sundsvall.licensedbusiness.integration.db.model.enums;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ACTIVE;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ENDED;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.fromValidTo;

class AssignmentStatusTest {

	private static final LocalDate TODAY = LocalDate.of(2026, 10, 27);

	@Test
	void openEndedAssignmentIsActive() {
		assertThat(fromValidTo(null, TODAY)).isEqualTo(ACTIVE);
	}

	@Test
	void lastValidDayIsInclusive() {
		assertThat(fromValidTo(TODAY, TODAY)).isEqualTo(ACTIVE);
	}

	@Test
	void assignmentIsEndedTheDayAfterItsLastValidDay() {
		assertThat(fromValidTo(TODAY.minusDays(1), TODAY)).isEqualTo(ENDED);
	}

	@Test
	void futureLastValidDayIsActive() {
		assertThat(fromValidTo(TODAY.plusYears(1), TODAY)).isEqualTo(ACTIVE);
	}
}
