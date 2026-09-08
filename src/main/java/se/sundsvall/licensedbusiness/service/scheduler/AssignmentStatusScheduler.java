package se.sundsvall.licensedbusiness.service.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberAssignmentRepository;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

@Service
public class AssignmentStatusScheduler {

	private final RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	public AssignmentStatusScheduler(final RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository) {
		this.restaurantNumberAssignmentRepository = restaurantNumberAssignmentRepository;
	}

	@Transactional
	@Dept44Scheduled(
		cron = "${schedulers.update-assignment-status.cron}",
		name = "${schedulers.update-assignment-status.name}",
		lockAtMostFor = "${schedulers.update-assignment-status.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${schedulers.update-assignment-status.maximum-execution-time}")
	public void updateExpiredAssignmentStatus() {
		final var today = LocalDate.now(ZoneId.systemDefault());

		restaurantNumberAssignmentRepository.findAllByStatusAndValidToBefore(AssignmentStatus.ACTIVE, today)
			.forEach(assignment -> {
				assignment.setStatus(AssignmentStatus.ENDED);
				restaurantNumberAssignmentRepository.save(assignment);
			});
	}
}
