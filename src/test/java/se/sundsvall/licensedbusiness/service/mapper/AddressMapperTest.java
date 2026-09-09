package se.sundsvall.licensedbusiness.service.mapper;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest {

	private final AddressMapper addressMapper = new AddressMapper();

	@Test
	void toAddress() {
		final var created = OffsetDateTime.now();
		final var entity = AddressEntity.create()
			.withId("address-1")
			.withStreetAddress("Storgatan 1")
			.withPostalCode("852 30")
			.withPostalArea("Sundsvall")
			.withMunicipalityId("2281")
			.withCreated(created);

		final var address = addressMapper.toAddress(entity);

		assertThat(address.getId()).isEqualTo("address-1");
		assertThat(address.getStreetAddress()).isEqualTo("Storgatan 1");
		assertThat(address.getPostalCode()).isEqualTo("852 30");
		assertThat(address.getPostalArea()).isEqualTo("Sundsvall");
		assertThat(address.getMunicipalityId()).isEqualTo("2281");
		assertThat(address.getCreated()).isEqualTo(created);
	}

	@Test
	void toAddressWithNullEntity() {
		assertThat(addressMapper.toAddress(null)).isNull();
	}
}
