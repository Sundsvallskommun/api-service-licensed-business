package se.sundsvall.licensedbusiness.integration.db.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;

@Repository
public interface RestaurantNumberRepository extends JpaRepository<RestaurantNumberEntity, String> {
	Optional<RestaurantNumberEntity> findByRestaurantNumber(String restaurantNumber);

	Optional<RestaurantNumberEntity> findByRestaurantNumberAndMunicipalityId(String restaurantNumber, String municipalityId);

	Optional<RestaurantNumberEntity> findByIdAndMunicipalityId(String id, String municipalityId);

	@Query("""
		SELECT rn.restaurantNumber FROM RestaurantNumberEntity rn
		WHERE rn.municipalityId = :municipalityId
		AND rn.restaurantNumber LIKE CONCAT(:municipalityId, '____')
		""")
	List<String> findSequencedRestaurantNumbers(@Param("municipalityId") String municipalityId);

	@Query("""
		SELECT DISTINCT rn FROM RestaurantNumberEntity rn
		WHERE rn.municipalityId = :municipalityId
		AND EXISTS (
		    SELECT 1 FROM RestaurantNumberAssignmentEntity latest
		    WHERE latest.restaurantNumber = rn
		    AND latest.address.id = :addressId
		    AND latest.validFrom = (SELECT MAX(a2.validFrom) FROM RestaurantNumberAssignmentEntity a2 WHERE a2.restaurantNumber = rn)
		)
		AND NOT EXISTS (
		    SELECT 1 FROM RestaurantNumberAssignmentEntity a
		    WHERE a.restaurantNumber = rn
		    AND a.status = se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ACTIVE
		    AND a.validFrom = (SELECT MAX(a2.validFrom) FROM RestaurantNumberAssignmentEntity a2 WHERE a2.restaurantNumber = rn)
		)
		""")
	List<RestaurantNumberEntity> findAvailableByMunicipalityIdAndAddressId(@Param("municipalityId") String municipalityId, @Param("addressId") String addressId);
}
