package se.sundsvall.licensedbusiness.service.mapper;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;

import static java.util.Collections.emptyList;

@Component
public class RestaurantNumberMapper {

	public RestaurantNumber toRestaurantNumber(final RestaurantNumberEntity entity) {
		return Optional.ofNullable(entity)
			.map(e -> RestaurantNumber.create()
				.withId(e.getId())
				.withNumber(e.getRestaurantNumber())
				.withMunicipalityId(e.getMunicipalityId())
				.withCreated(e.getCreated()))
			.orElse(null);
	}

	public List<RestaurantNumber> toRestaurantNumbers(final List<RestaurantNumberEntity> entities) {
		return Optional.ofNullable(entities).orElse(emptyList()).stream()
			.map(this::toRestaurantNumber)
			.toList();
	}
}
