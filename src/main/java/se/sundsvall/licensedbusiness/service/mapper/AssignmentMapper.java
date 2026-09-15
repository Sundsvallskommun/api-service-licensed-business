package se.sundsvall.licensedbusiness.service.mapper;

import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.licensedbusiness.api.model.Assignment;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;

@Component
public class AssignmentMapper {

	private final RestaurantNumberMapper restaurantNumberMapper;

	private final AddressMapper addressMapper;

	private final LicenseHolderMapper licenseHolderMapper;

	public AssignmentMapper(final RestaurantNumberMapper restaurantNumberMapper, final AddressMapper addressMapper, final LicenseHolderMapper licenseHolderMapper) {
		this.restaurantNumberMapper = restaurantNumberMapper;
		this.addressMapper = addressMapper;
		this.licenseHolderMapper = licenseHolderMapper;
	}

	public Assignment toAssignment(final RestaurantNumberAssignmentEntity entity) {
		return Optional.ofNullable(entity)
			.map(e -> Assignment.create()
				.withId(e.getId())
				.withRestaurantNumber(restaurantNumberMapper.toRestaurantNumber(e.getRestaurantNumber()))
				.withAddress(addressMapper.toAddress(e.getAddress()))
				.withLicenseHolder(licenseHolderMapper.toLicenseHolder(e.getLicenseHolder()))
				.withHolderName(e.getHolderName())
				.withPremisesName(e.getPremisesName())
				.withValidFrom(e.getValidFrom())
				.withValidTo(e.getValidTo())
				.withStatus(e.getStatus())
				.withCreated(e.getCreated()))
			.orElse(null);
	}
}
