package se.sundsvall.licensedbusiness.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.api.model.AddressPagingParameters;
import se.sundsvall.licensedbusiness.api.model.AddressSearchParameters;
import se.sundsvall.licensedbusiness.api.model.Addresses;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.service.mapper.AddressMapper;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.licensedbusiness.service.AddressNormalizer.normalizePostalCode;
import static se.sundsvall.licensedbusiness.service.AddressNormalizer.normalizeStreetAddress;
import static se.sundsvall.licensedbusiness.service.TextSanitizer.sanitize;

@Service
public class AddressService {

	private final AddressRepository addressRepository;

	private final AddressMapper addressMapper;

	public AddressService(final AddressRepository addressRepository, final AddressMapper addressMapper) {
		this.addressRepository = addressRepository;
		this.addressMapper = addressMapper;
	}

	@Transactional
	public String createAddress(final String municipalityId, final Address address) {
		final var streetAddress = normalizeStreetAddress(address.getStreetAddress());
		final var postalCode = normalizePostalCode(address.getPostalCode());

		addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId(streetAddress, postalCode, municipalityId)
			.ifPresent(existing -> {
				throw Problem.valueOf(CONFLICT, "Address %s, %s already exists with ID %s".formatted(sanitize(streetAddress), sanitize(postalCode), sanitize(existing.getId())));
			});

		return addressRepository.save(AddressEntity.create()
			.withStreetAddress(streetAddress)
			.withPostalCode(postalCode)
			.withPostalArea(Optional.ofNullable(address.getPostalArea()).map(String::trim).orElse(null))
			.withMunicipalityId(municipalityId))
			.getId();
	}

	public Address getAddress(final String municipalityId, final String addressId) {
		return addressRepository.findById(addressId)
			.filter(entity -> municipalityId.equals(entity.getMunicipalityId()))
			.map(addressMapper::toAddress)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Address %s not found".formatted(sanitize(addressId))));
	}

	public Addresses getAddresses(final String municipalityId, final AddressPagingParameters pagingParameters) {
		final var page = addressRepository.findAllByMunicipalityId(municipalityId, toPageable(pagingParameters));
		return toAddresses(page);
	}

	public Addresses searchAddresses(final String municipalityId, final AddressSearchParameters searchParameters) {
		final var page = addressRepository.findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase(municipalityId, searchParameters.getQuery(), toPageable(searchParameters));
		return toAddresses(page);
	}

	private static Pageable toPageable(final AbstractParameterPagingAndSortingBase pagingParameters) {
		return PageRequest.of(pagingParameters.getPage() - 1, pagingParameters.getLimit(), pagingParameters.sort());
	}

	private Addresses toAddresses(final Page<AddressEntity> page) {
		return Addresses.create()
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page))
			.withContent(addressMapper.toAddresses(page.getContent()));
	}
}
