package se.sundsvall.licensedbusiness.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.api.model.AddressPagingParameters;
import se.sundsvall.licensedbusiness.api.model.Addresses;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.service.mapper.AddressMapper;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AddressService {

	private final AddressRepository addressRepository;

	private final AddressMapper addressMapper;

	public AddressService(final AddressRepository addressRepository, final AddressMapper addressMapper) {
		this.addressRepository = addressRepository;
		this.addressMapper = addressMapper;
	}

	public Address getAddress(final String municipalityId, final String addressId) {
		return addressRepository.findById(addressId)
			.filter(entity -> municipalityId.equals(entity.getMunicipalityId()))
			.map(addressMapper::toAddress)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Address not found"));
	}

	public Addresses getAddresses(final String municipalityId, final AddressPagingParameters pagingParameters) {
		final var pageable = PageRequest.of(pagingParameters.getPage() - 1, pagingParameters.getLimit(), pagingParameters.sort());
		final var page = addressRepository.findAllByMunicipalityId(municipalityId, pageable);
		return Addresses.create()
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page))
			.withAddresses(addressMapper.toAddresses(page.getContent()));
	}
}
