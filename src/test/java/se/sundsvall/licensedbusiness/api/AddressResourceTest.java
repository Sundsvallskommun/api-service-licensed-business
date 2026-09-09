package se.sundsvall.licensedbusiness.api;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.service.AddressService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "123e4567-e89b-12d3-a456-426614174000";

	@Mock
	private AddressService addressService;

	@Test
	void getAddresses() {
		final var addressResource = new AddressResource(addressService);

		assertThatThrownBy(() -> addressResource.getAddresses(MUNICIPALITY_ID, Pageable.unpaged()))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}

	@Test
	void getAddress() {
		final var expected = Address.create().withId(ADDRESS_ID).withStreetAddress("Storgatan 1").withPostalCode("852 30").withPostalArea("Sundsvall").withMunicipalityId(MUNICIPALITY_ID).withCreated(OffsetDateTime.now());
		when(addressService.getAddress(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(expected);

		final var addressResource = new AddressResource(addressService);
		final var response = addressResource.getAddress(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(expected);
		verify(addressService).getAddress(MUNICIPALITY_ID, ADDRESS_ID);
	}

	@Test
	void searchAddresses() {
		final var addressResource = new AddressResource(addressService);

		assertThatThrownBy(() -> addressResource.searchAddresses(MUNICIPALITY_ID, "Storgatan", Pageable.unpaged()))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}

	@Test
	void createAddress() {
		final var addressResource = new AddressResource(addressService);

		assertThatThrownBy(() -> addressResource.createAddress(MUNICIPALITY_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}
}
