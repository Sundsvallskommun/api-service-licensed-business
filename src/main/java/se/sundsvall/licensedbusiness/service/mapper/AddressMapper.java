package se.sundsvall.licensedbusiness.service.mapper;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;

import static java.util.Collections.emptyList;

@Component
public class AddressMapper {

	public Address toAddress(final AddressEntity entity) {
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

	public List<Address> toAddresses(final List<AddressEntity> entities) {
		return Optional.ofNullable(entities).orElse(emptyList()).stream()
			.map(this::toAddress)
			.toList();
	}
}
