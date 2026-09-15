package se.sundsvall.licensedbusiness.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.api.model.AddressPagingParameters;
import se.sundsvall.licensedbusiness.api.model.AddressSearchParameters;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.service.mapper.AddressMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "address-1";

	@Mock
	private AddressRepository addressRepository;

	private final AddressMapper addressMapper = new AddressMapper();

	@Test
	void createAddressNormalizesBeforeLookupAndInsert() {
		when(addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId("Storgatan 1", "852 30", MUNICIPALITY_ID)).thenReturn(Optional.empty());
		when(addressRepository.save(any())).thenAnswer(invocation -> ((AddressEntity) invocation.getArgument(0)).withId(ADDRESS_ID));

		final var addressService = new AddressService(addressRepository, addressMapper);
		final var result = addressService.createAddress(MUNICIPALITY_ID, Address.create()
			.withStreetAddress("  Storgatan   1 ")
			.withPostalCode("85230")
			.withPostalArea(" Sundsvall "));

		assertThat(result).isEqualTo(ADDRESS_ID);

		final var captor = ArgumentCaptor.forClass(AddressEntity.class);
		verify(addressRepository).save(captor.capture());
		assertThat(captor.getValue().getStreetAddress()).isEqualTo("Storgatan 1");
		assertThat(captor.getValue().getPostalCode()).isEqualTo("852 30");
		assertThat(captor.getValue().getPostalArea()).isEqualTo("Sundsvall");
		assertThat(captor.getValue().getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
	}

	@Test
	void createAddressWithoutPostalArea() {
		when(addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId("Storgatan 1", "852 30", MUNICIPALITY_ID)).thenReturn(Optional.empty());
		when(addressRepository.save(any())).thenAnswer(invocation -> ((AddressEntity) invocation.getArgument(0)).withId(ADDRESS_ID));

		final var addressService = new AddressService(addressRepository, addressMapper);
		addressService.createAddress(MUNICIPALITY_ID, Address.create().withStreetAddress("Storgatan 1").withPostalCode("852 30"));

		final var captor = ArgumentCaptor.forClass(AddressEntity.class);
		verify(addressRepository).save(captor.capture());
		assertThat(captor.getValue().getPostalArea()).isNull();
	}

	@Test
	void createAddressWithExistingAddress() {
		when(addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId("Storgatan 1", "852 30", MUNICIPALITY_ID))
			.thenReturn(Optional.of(AddressEntity.create().withId(ADDRESS_ID)));

		final var addressService = new AddressService(addressRepository, addressMapper);
		final var address = Address.create().withStreetAddress("Storgatan 1").withPostalCode("85230");

		assertThatThrownBy(() -> addressService.createAddress(MUNICIPALITY_ID, address))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Address Storgatan 1, 852 30 already exists with ID " + ADDRESS_ID)
			.extracting("status").isEqualTo(CONFLICT);
		verify(addressRepository, never()).save(any());
	}

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
			.hasMessageContaining("Address " + ADDRESS_ID + " not found");
	}

	@Test
	void getAddressWrongMunicipality() {
		final var entity = AddressEntity.create().withId(ADDRESS_ID).withMunicipalityId("9999");
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(entity));

		final var addressService = new AddressService(addressRepository, addressMapper);

		assertThatThrownBy(() -> addressService.getAddress(MUNICIPALITY_ID, ADDRESS_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Address " + ADDRESS_ID + " not found");
	}

	@Test
	void getAddresses() {
		final var entity = AddressEntity.create().withId(ADDRESS_ID).withStreetAddress("Storgatan 1").withMunicipalityId(MUNICIPALITY_ID);
		final var pagingParameters = new AddressPagingParameters();
		pagingParameters.setPage(1);
		pagingParameters.setLimit(20);
		final var expectedPageable = PageRequest.of(0, 20, Sort.unsorted());
		final var page = new PageImpl<>(List.of(entity), expectedPageable, 1);
		when(addressRepository.findAllByMunicipalityId(MUNICIPALITY_ID, expectedPageable)).thenReturn(page);

		final var addressService = new AddressService(addressRepository, addressMapper);
		final var result = addressService.getAddresses(MUNICIPALITY_ID, pagingParameters);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getId()).isEqualTo(ADDRESS_ID);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
	}

	@Test
	void searchAddresses() {
		final var entity = AddressEntity.create().withId(ADDRESS_ID).withStreetAddress("Storgatan 1").withMunicipalityId(MUNICIPALITY_ID);
		final var searchParameters = new AddressSearchParameters();
		searchParameters.setQuery("Storgatan");
		searchParameters.setPage(1);
		searchParameters.setLimit(20);
		final var expectedPageable = PageRequest.of(0, 20, Sort.unsorted());
		final var page = new PageImpl<>(List.of(entity), expectedPageable, 1);
		when(addressRepository.findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase(MUNICIPALITY_ID, "Storgatan", expectedPageable)).thenReturn(page);

		final var addressService = new AddressService(addressRepository, addressMapper);
		final var result = addressService.searchAddresses(MUNICIPALITY_ID, searchParameters);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getId()).isEqualTo(ADDRESS_ID);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
	}
}
