package se.sundsvall.licensedbusiness.integration.db.dao;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

@CircuitBreaker(name = "restaurantNumberAssignmentRepository")
public interface RestaurantNumberAssignmentRepository extends JpaRepository<RestaurantNumberAssignmentEntity, String> {

	List<RestaurantNumberAssignmentEntity> findAllByStatusAndValidToBefore(AssignmentStatus status, LocalDate date);

	@EntityGraph(attributePaths = {
		"restaurantNumber", "address", "licenseHolder"
	})
	Optional<RestaurantNumberAssignmentEntity> findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(RestaurantNumberEntity restaurantNumber);

	@EntityGraph(attributePaths = {
		"restaurantNumber", "address", "licenseHolder"
	})
	Optional<RestaurantNumberAssignmentEntity> findByIdAndRestaurantNumber_MunicipalityId(String id, String municipalityId);

	List<RestaurantNumberAssignmentEntity> findAllByRestaurantNumberAndStatus(RestaurantNumberEntity restaurantNumber, AssignmentStatus status);

	List<RestaurantNumberAssignmentEntity> findAllByRestaurantNumber(RestaurantNumberEntity restaurantNumber);
}
