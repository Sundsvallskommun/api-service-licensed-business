package se.sundsvall.licensedbusiness.integration.db.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;

@Repository
public interface RestaurantNumberRepository extends JpaRepository<RestaurantNumberEntity, String> {
	Optional<RestaurantNumberEntity> findByRestaurantNumber(String restaurantNumber);
}
