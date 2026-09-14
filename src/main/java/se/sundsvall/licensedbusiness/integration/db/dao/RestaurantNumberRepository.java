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

	// "Available for an address" means the restaurant number's most recent assignment (highest validFrom)
	// was at that address and is not ACTIVE. A number whose latest assignment moved to another address
	// belongs to that address now, and a number without any assignment belongs to no address.
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
