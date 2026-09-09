package se.sundsvall.licensedbusiness.integration.db.dao;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

@Repository
public interface RestaurantNumberAssignmentRepository extends JpaRepository<RestaurantNumberAssignmentEntity, String> {
	List<RestaurantNumberAssignmentEntity> findAllByStatusAndValidToBefore(AssignmentStatus status, LocalDate date);
}
