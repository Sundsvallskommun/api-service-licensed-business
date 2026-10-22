package se.sundsvall.licensedbusiness.service.mapper;

import java.util.Optional;
import se.sundsvall.licensedbusiness.api.model.Assignment;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;

import static se.sundsvall.licensedbusiness.service.mapper.AddressMapper.toAddress;
import static se.sundsvall.licensedbusiness.service.mapper.LicenseHolderMapper.toLicenseHolder;
import static se.sundsvall.licensedbusiness.service.mapper.RestaurantNumberMapper.toRestaurantNumber;

public final class AssignmentMapper {

	private AssignmentMapper() {}

	public static Assignment toAssignment(final RestaurantNumberAssignmentEntity entity) {
		return Optional.ofNullable(entity)
			.map(e -> Assignment.create()
				.withId(e.getId())
				.withRestaurantNumber(toRestaurantNumber(e.getRestaurantNumber()))
				.withAddress(toAddress(e.getAddress()))
				.withLicenseHolder(toLicenseHolder(e.getLicenseHolder()))
				.withHolderName(e.getHolderName())
				.withPremisesName(e.getPremisesName())
				.withValidFrom(e.getValidFrom())
				.withValidTo(e.getValidTo())
				.withStatus(Optional.ofNullable(e.getStatus()).map(Enum::name).orElse(null))
				.withCreated(e.getCreated()))
			.orElse(null);
	}
}
