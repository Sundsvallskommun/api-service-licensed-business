package se.sundsvall.licensedbusiness.integration.db.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

@Repository
public interface RestaurantNumberAssignmentRepository extends JpaRepository<RestaurantNumberAssignmentEntity, String> {

	List<RestaurantNumberAssignmentEntity> findAllByStatus(AssignmentStatus status);

	@EntityGraph(attributePaths = {
		"restaurantNumber", "address", "licenseHolder"
	})
	Optional<RestaurantNumberAssignmentEntity> findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(RestaurantNumberEntity restaurantNumber);

	@EntityGraph(attributePaths = {
		"restaurantNumber", "address", "licenseHolder"
	})
	Optional<RestaurantNumberAssignmentEntity> findByIdAndRestaurantNumber_MunicipalityId(String id, String municipalityId);

	List<RestaurantNumberAssignmentEntity> findAllByRestaurantNumberAndStatus(RestaurantNumberEntity restaurantNumber, AssignmentStatus status);
}
