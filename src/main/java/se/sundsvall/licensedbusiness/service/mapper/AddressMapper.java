package se.sundsvall.licensedbusiness.service.mapper;

import java.util.List;
import java.util.Optional;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;

import static java.util.Collections.emptyList;

public final class AddressMapper {

	private AddressMapper() {}

	public static Address toAddress(final AddressEntity entity) {
		return Optional.ofNullable(entity)
			.map(e -> Address.create()
				.withId(e.getId())
				.withStreetAddress(e.getStreetAddress())
				.withPostalCode(e.getPostalCode())
				.withPostalArea(e.getPostalArea())
				.withMunicipalityId(e.getMunicipalityId())
				.withCreated(e.getCreated()))
			.orElse(null);
	}

	public static List<Address> toAddresses(final List<AddressEntity> entities) {
		return Optional.ofNullable(entities).orElse(emptyList()).stream()
			.map(AddressMapper::toAddress)
			.toList();
	}
}
