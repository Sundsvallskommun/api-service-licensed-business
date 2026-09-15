package se.sundsvall.licensedbusiness.service.mapper;

import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.licensedbusiness.api.model.LicenseHolder;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;

@Component
public class LicenseHolderMapper {

	public LicenseHolder toLicenseHolder(final LicenseHolderEntity entity) {
		return Optional.ofNullable(entity)
			.map(e -> LicenseHolder.create()
				.withId(e.getId())
				.withOrgNumber(e.getOrgNumber())
				.withName(e.getName())
				.withCreated(e.getCreated()))
			.orElse(null);
	}
}
