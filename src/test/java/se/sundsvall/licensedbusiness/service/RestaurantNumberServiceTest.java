package se.sundsvall.licensedbusiness.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.service.mapper.RestaurantNumberMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantNumberServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "address-1";

	@Mock
	private RestaurantNumberRepository restaurantNumberRepository;

	private final RestaurantNumberMapper restaurantNumberMapper = new RestaurantNumberMapper();

	@Test
	void getAvailableRestaurantNumbers() {
		final var entity = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("1001").withMunicipalityId(MUNICIPALITY_ID);
		when(restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(List.of(entity));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo("number-1");
		assertThat(result.getFirst().getNumber()).isEqualTo("1001");
	}

	@Test
	void getAvailableRestaurantNumbersWithNoMatch() {
		when(restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(List.of());

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(result).isEmpty();
	}
}
