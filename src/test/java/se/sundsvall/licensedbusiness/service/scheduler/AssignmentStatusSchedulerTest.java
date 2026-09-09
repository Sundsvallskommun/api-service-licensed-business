package se.sundsvall.licensedbusiness.service.scheduler;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberAssignmentRepository;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignmentStatusSchedulerTest {

	@Mock
	private RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	@Test
	void updateExpiredAssignmentStatus() {
		final var expired1 = RestaurantNumberAssignmentEntity.create().withId("1").withStatus(AssignmentStatus.ACTIVE).withValidTo(LocalDate.now().minusDays(1));
		final var expired2 = RestaurantNumberAssignmentEntity.create().withId("2").withStatus(AssignmentStatus.ACTIVE).withValidTo(LocalDate.now().minusDays(10));

		when(restaurantNumberAssignmentRepository.findAllByStatusAndValidToBefore(eq(AssignmentStatus.ACTIVE), any()))
			.thenReturn(List.of(expired1, expired2));

		final var scheduler = new AssignmentStatusScheduler(restaurantNumberAssignmentRepository);
		scheduler.updateExpiredAssignmentStatus();

		final var captor = ArgumentCaptor.forClass(RestaurantNumberAssignmentEntity.class);
		verify(restaurantNumberAssignmentRepository, times(2)).save(captor.capture());
		assertThat(captor.getAllValues()).extracting(RestaurantNumberAssignmentEntity::getId).containsExactlyInAnyOrder("1", "2");
		assertThat(captor.getAllValues()).allSatisfy(entity -> assertThat(entity.getStatus()).isEqualTo(AssignmentStatus.ENDED));
	}

	@Test
	void updateExpiredAssignmentStatusWithNoExpired() {
		when(restaurantNumberAssignmentRepository.findAllByStatusAndValidToBefore(eq(AssignmentStatus.ACTIVE), any()))
			.thenReturn(List.of());

		final var scheduler = new AssignmentStatusScheduler(restaurantNumberAssignmentRepository);
		scheduler.updateExpiredAssignmentStatus();

		verify(restaurantNumberAssignmentRepository, times(0)).save(any());
	}
}
