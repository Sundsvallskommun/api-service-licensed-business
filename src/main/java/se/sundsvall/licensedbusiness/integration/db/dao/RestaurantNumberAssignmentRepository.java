package se.sundsvall.licensedbusiness.integration.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.licensedbusiness.integration.db.RestaurantNumberAssignmentEntity;

@Repository
public interface RestaurantNumberAssignmentRepository extends JpaRepository<RestaurantNumberAssignmentEntity, String> {
}
