package se.sundsvall.licensedbusiness.service.mapper;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.licensedbusiness.api.model.AddressRestaurantNumber;
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
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
				.withReported(e.getReported())
				.withCreated(e.getCreated()))
			.orElse(null);
	}

	public AddressRestaurantNumber toAddressRestaurantNumber(final RestaurantNumberEntity entity, final RestaurantNumberAssignmentEntity latestAssignment, final String status) {
		final var latest = Optional.ofNullable(latestAssignment);

		return AddressRestaurantNumber.create()
			.withId(entity.getId())
			.withNumber(entity.getRestaurantNumber())
			.withMunicipalityId(entity.getMunicipalityId())
			.withReported(entity.getReported())
			.withStatus(status)
			.withPremisesName(latest.map(RestaurantNumberAssignmentEntity::getPremisesName).orElse(null))
			.withValidFrom(latest.map(RestaurantNumberAssignmentEntity::getValidFrom).orElse(null))
			.withValidTo(latest.map(RestaurantNumberAssignmentEntity::getValidTo).orElse(null))
			.withCreated(entity.getCreated());
	}

	public List<RestaurantNumber> toRestaurantNumbers(final List<RestaurantNumberEntity> entities) {
		return Optional.ofNullable(entities).orElse(emptyList()).stream()
			.map(this::toRestaurantNumber)
			.toList();
	}
}
