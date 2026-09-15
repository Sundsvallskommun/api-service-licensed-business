package se.sundsvall.licensedbusiness.service;

import java.util.List;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.service.mapper.RestaurantNumberMapper;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class RestaurantNumberService {

	private final RestaurantNumberRepository restaurantNumberRepository;

	private final AddressRepository addressRepository;

	private final RestaurantNumberMapper restaurantNumberMapper;

	public RestaurantNumberService(final RestaurantNumberRepository restaurantNumberRepository, final AddressRepository addressRepository, final RestaurantNumberMapper restaurantNumberMapper) {
		this.restaurantNumberRepository = restaurantNumberRepository;
		this.addressRepository = addressRepository;
		this.restaurantNumberMapper = restaurantNumberMapper;
	}

	public List<RestaurantNumber> getAvailableRestaurantNumbers(final String municipalityId, final String addressId) {
		if (!addressRepository.existsByIdAndMunicipalityId(addressId, municipalityId)) {
			throw Problem.valueOf(NOT_FOUND, "Address not found");
		}

		final var entities = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(municipalityId, addressId);
		return restaurantNumberMapper.toRestaurantNumbers(entities);
	}
}
