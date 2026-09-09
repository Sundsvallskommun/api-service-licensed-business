package se.sundsvall.licensedbusiness.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.service.mapper.AddressMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "address-1";

	@Mock
	private AddressRepository addressRepository;

	private final AddressMapper addressMapper = new AddressMapper();

	@Test
	void getAddress() {
		final var created = OffsetDateTime.now();
		final var entity = AddressEntity.create()
			.withId(ADDRESS_ID)
			.withStreetAddress("Storgatan 1")
			.withPostalCode("852 30")
			.withPostalArea("Sundsvall")
			.withMunicipalityId(MUNICIPALITY_ID)
			.withCreated(created);
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(entity));

		final var addressService = new AddressService(addressRepository, addressMapper);
		final var result = addressService.getAddress(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(result.getId()).isEqualTo(ADDRESS_ID);
		assertThat(result.getStreetAddress()).isEqualTo("Storgatan 1");
		assertThat(result.getPostalCode()).isEqualTo("852 30");
		assertThat(result.getPostalArea()).isEqualTo("Sundsvall");
		assertThat(result.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(result.getCreated()).isEqualTo(created);
	}

	@Test
	void getAddressNotFound() {
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.empty());

		final var addressService = new AddressService(addressRepository, addressMapper);

		assertThatThrownBy(() -> addressService.getAddress(MUNICIPALITY_ID, ADDRESS_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Address not found");
	}

	@Test
	void getAddressWrongMunicipality() {
		final var entity = AddressEntity.create().withId(ADDRESS_ID).withMunicipalityId("9999");
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(entity));

		final var addressService = new AddressService(addressRepository, addressMapper);

		assertThatThrownBy(() -> addressService.getAddress(MUNICIPALITY_ID, ADDRESS_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Address not found");
	}
}
