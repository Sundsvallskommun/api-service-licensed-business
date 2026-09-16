package se.sundsvall.licensedbusiness.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.service.mapper.RestaurantNumberMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class RestaurantNumberServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "address-1";

	@Mock
	private RestaurantNumberRepository restaurantNumberRepository;

	@Mock
	private AddressRepository addressRepository;

	private final RestaurantNumberMapper restaurantNumberMapper = new RestaurantNumberMapper();

	@Test
	void getRestaurantNumber() {
		final var entity = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("22810001").withMunicipalityId(MUNICIPALITY_ID);
		when(restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId("22810001", MUNICIPALITY_ID)).thenReturn(Optional.of(entity));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, addressRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getRestaurantNumber(MUNICIPALITY_ID, "22810001");

		assertThat(result.getId()).isEqualTo("number-1");
		assertThat(result.getNumber()).isEqualTo("22810001");
	}

	@Test
	void getRestaurantNumberNotFound() {
		when(restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId("22810001", MUNICIPALITY_ID)).thenReturn(Optional.empty());

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, addressRepository, restaurantNumberMapper);

		assertThatThrownBy(() -> restaurantNumberService.getRestaurantNumber(MUNICIPALITY_ID, "22810001"))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Restaurant number 22810001 not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void createRestaurantNumberStartsAtOneInAnEmptyRegister() {
		when(restaurantNumberRepository.findSequencedRestaurantNumbers(MUNICIPALITY_ID)).thenReturn(List.of());
		when(restaurantNumberRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, addressRepository, restaurantNumberMapper);

		assertThat(restaurantNumberService.createRestaurantNumber(MUNICIPALITY_ID)).isEqualTo("22810001");
	}

	@Test
	void createRestaurantNumberFillsTheLowestGapAndIgnoresNumbersOutsideTheFormat() {
		when(restaurantNumberRepository.findSequencedRestaurantNumbers(MUNICIPALITY_ID)).thenReturn(List.of("22810001", "22810003", "22819814", "2281037x"));
		when(restaurantNumberRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, addressRepository, restaurantNumberMapper);

		assertThat(restaurantNumberService.createRestaurantNumber(MUNICIPALITY_ID)).isEqualTo("22810002");

		final var captor = ArgumentCaptor.forClass(RestaurantNumberEntity.class);
		verify(restaurantNumberRepository).saveAndFlush(captor.capture());
		assertThat(captor.getValue().getRestaurantNumber()).isEqualTo("22810002");
		assertThat(captor.getValue().getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
	}

	@Test
	void createRestaurantNumberRetriesWhenAnotherRequestTookTheNumber() {
		when(restaurantNumberRepository.findSequencedRestaurantNumbers(MUNICIPALITY_ID))
			.thenReturn(List.of("22810001"))
			.thenReturn(List.of("22810001", "22810002"));
		when(restaurantNumberRepository.saveAndFlush(any()))
			.thenThrow(new DataIntegrityViolationException("duplicate"))
			.thenAnswer(invocation -> invocation.getArgument(0));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, addressRepository, restaurantNumberMapper);

		assertThat(restaurantNumberService.createRestaurantNumber(MUNICIPALITY_ID)).isEqualTo("22810003");
	}

	@Test
	void createRestaurantNumberGivesUpAfterThreeAttempts() {
		when(restaurantNumberRepository.findSequencedRestaurantNumbers(MUNICIPALITY_ID)).thenReturn(List.of());
		when(restaurantNumberRepository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, addressRepository, restaurantNumberMapper);

		assertThatThrownBy(() -> restaurantNumberService.createRestaurantNumber(MUNICIPALITY_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Could not allocate a restaurant number")
			.extracting("status").isEqualTo(CONFLICT);
		verify(restaurantNumberRepository, times(3)).saveAndFlush(any());
	}

	@Test
	void createRestaurantNumberMustNotRunInATransaction() throws NoSuchMethodException {
		final var reason = """
			createRestaurantNumber retries after a unique constraint violation, and that only works when every \
			attempt commits or fails on its own. Inside a transaction the first violation marks it rollback only, \
			every later attempt fails too, and the endpoint starts answering 409 as soon as two requests overlap. \
			Keep the method and its class free of @Transactional, and do not call it from a transactional method.""";

		final var method = RestaurantNumberService.class.getMethod("createRestaurantNumber", String.class);

		assertThat(method.getAnnotation(Transactional.class)).as(reason).isNull();
		assertThat(method.getAnnotation(jakarta.transaction.Transactional.class)).as(reason).isNull();
		assertThat(RestaurantNumberService.class.getAnnotation(Transactional.class)).as(reason).isNull();
		assertThat(RestaurantNumberService.class.getAnnotation(jakarta.transaction.Transactional.class)).as(reason).isNull();
	}

	@Test
	void getAvailableRestaurantNumbers() {
		final var entity = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("1001").withMunicipalityId(MUNICIPALITY_ID);
		when(addressRepository.existsByIdAndMunicipalityId(ADDRESS_ID, MUNICIPALITY_ID)).thenReturn(true);
		when(restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(List.of(entity));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, addressRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo("number-1");
		assertThat(result.getFirst().getNumber()).isEqualTo("1001");
	}

	@Test
	void getAvailableRestaurantNumbersWithNoMatch() {
		when(addressRepository.existsByIdAndMunicipalityId(ADDRESS_ID, MUNICIPALITY_ID)).thenReturn(true);
		when(restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(List.of());

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, addressRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(result).isEmpty();
	}

	@Test
	void getAvailableRestaurantNumbersWithUnknownAddress() {
		when(addressRepository.existsByIdAndMunicipalityId(ADDRESS_ID, MUNICIPALITY_ID)).thenReturn(false);

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, addressRepository, restaurantNumberMapper);

		assertThatThrownBy(() -> restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Address " + ADDRESS_ID + " not found")
			.extracting("status").isEqualTo(NOT_FOUND);
		verifyNoInteractions(restaurantNumberRepository);
	}
}
