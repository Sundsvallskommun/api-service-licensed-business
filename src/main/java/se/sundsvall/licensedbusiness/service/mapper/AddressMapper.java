package se.sundsvall.licensedbusiness.service.mapper;

import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;

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
}
