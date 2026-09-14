package se.sundsvall.licensedbusiness.service;

import java.util.List;
import org.springframework.stereotype.Service;
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.service.mapper.RestaurantNumberMapper;

@Service
public class RestaurantNumberService {

	private final RestaurantNumberRepository restaurantNumberRepository;

	private final RestaurantNumberMapper restaurantNumberMapper;

	public RestaurantNumberService(final RestaurantNumberRepository restaurantNumberRepository, final RestaurantNumberMapper restaurantNumberMapper) {
		this.restaurantNumberRepository = restaurantNumberRepository;
		this.restaurantNumberMapper = restaurantNumberMapper;
	}

	public List<RestaurantNumber> getAvailableRestaurantNumbers(final String municipalityId, final String addressId) {
		final var entities = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(municipalityId, addressId);
		return restaurantNumberMapper.toRestaurantNumbers(entities);
	}
}
