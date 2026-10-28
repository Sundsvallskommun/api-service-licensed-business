package se.sundsvall.licensedbusiness.service.scheduler;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberAssignmentRepository;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignmentStatusSchedulerTest {

	@Mock
	private RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	@Spy
	private Clock clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneId.of("Europe/Stockholm"));

	@InjectMocks
	private AssignmentStatusScheduler scheduler;

	@Test
	void updateExpiredAssignmentStatus() {
		final var expired1 = RestaurantNumberAssignmentEntity.create().withId("1").withStatus(AssignmentStatus.ACTIVE).withValidTo(LocalDate.now(clock).minusDays(1));
		final var expired2 = RestaurantNumberAssignmentEntity.create().withId("2").withStatus(AssignmentStatus.ACTIVE).withValidTo(LocalDate.now(clock).minusDays(10));

		when(restaurantNumberAssignmentRepository.findAllByStatusAndValidToBefore(AssignmentStatus.ACTIVE, LocalDate.of(2026, 10, 27)))
			.thenReturn(List.of(expired1, expired2));

		scheduler.updateExpiredAssignmentStatus();

		assertThat(List.of(expired1, expired2)).allSatisfy(entity -> assertThat(entity.getStatus()).isEqualTo(AssignmentStatus.ENDED));
		verify(restaurantNumberAssignmentRepository).findAllByStatusAndValidToBefore(AssignmentStatus.ACTIVE, LocalDate.of(2026, 10, 27));
		verifyNoMoreInteractions(restaurantNumberAssignmentRepository);
	}

	@Test
	void updateExpiredAssignmentStatusWithNoExpired() {
		when(restaurantNumberAssignmentRepository.findAllByStatusAndValidToBefore(AssignmentStatus.ACTIVE, LocalDate.of(2026, 10, 27)))
			.thenReturn(List.of());

		scheduler.updateExpiredAssignmentStatus();

		verify(restaurantNumberAssignmentRepository).findAllByStatusAndValidToBefore(AssignmentStatus.ACTIVE, LocalDate.of(2026, 10, 27));
		verifyNoMoreInteractions(restaurantNumberAssignmentRepository);
	}
}
