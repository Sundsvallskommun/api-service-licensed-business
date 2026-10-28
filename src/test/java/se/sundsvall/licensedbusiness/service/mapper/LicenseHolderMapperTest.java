package se.sundsvall.licensedbusiness.service.mapper;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseHolderMapperTest {

	@Test
	void toLicenseHolder() {
		final var created = OffsetDateTime.now();
		final var entity = LicenseHolderEntity.create()
			.withId("holder-1")
			.withOrgNumber("556612-4144")
			.withName("Restaurang i Sundsvall AB")
			.withCreated(created);

		final var licenseHolder = LicenseHolderMapper.toLicenseHolder(entity);

		assertThat(licenseHolder.getId()).isEqualTo("holder-1");
		assertThat(licenseHolder.getOrgNumber()).isEqualTo("556612-4144");
		assertThat(licenseHolder.getName()).isEqualTo("Restaurang i Sundsvall AB");
		assertThat(licenseHolder.getCreated()).isEqualTo(created);
	}

	@Test
	void toLicenseHolderWithNullEntity() {
		assertThat(LicenseHolderMapper.toLicenseHolder(null)).isNull();
	}
}
