package se.sundsvall.licensedbusiness.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
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
import se.sundsvall.licensedbusiness.api.model.AddressRestaurantNumber;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberAssignmentRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;
import se.sundsvall.licensedbusiness.service.mapper.RestaurantNumberMapper;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ACTIVE;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ENDED;

@ExtendWith(MockitoExtension.class)
class RestaurantNumberServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "address-1";

	@Mock
	private RestaurantNumberRepository restaurantNumberRepository;

	@Mock
	private RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	@Mock
	private AddressRepository addressRepository;

	private final RestaurantNumberMapper restaurantNumberMapper = new RestaurantNumberMapper();

	@Test
	void getRestaurantNumber() {
		final var entity = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("22810001").withMunicipalityId(MUNICIPALITY_ID);
		when(restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId("22810001", MUNICIPALITY_ID)).thenReturn(Optional.of(entity));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getRestaurantNumber(MUNICIPALITY_ID, "22810001");

		assertThat(result.getId()).isEqualTo("number-1");
		assertThat(result.getNumber()).isEqualTo("22810001");
	}

	@Test
	void getRestaurantNumberNotFound() {
		when(restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId("22810001", MUNICIPALITY_ID)).thenReturn(Optional.empty());

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);

		assertThatThrownBy(() -> restaurantNumberService.getRestaurantNumber(MUNICIPALITY_ID, "22810001"))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Restaurant number 22810001 not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void createRestaurantNumberStartsAtOneInAnEmptyRegister() {
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(AddressEntity.create().withId(ADDRESS_ID).withMunicipalityId(MUNICIPALITY_ID)));
		when(restaurantNumberRepository.findSequencedRestaurantNumbers(MUNICIPALITY_ID)).thenReturn(List.of());
		when(restaurantNumberRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);

		assertThat(restaurantNumberService.createRestaurantNumber(MUNICIPALITY_ID, ADDRESS_ID)).isEqualTo("22810001");
	}

	@Test
	void createRestaurantNumberFillsTheLowestGapAndIgnoresNumbersOutsideTheFormat() {
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(AddressEntity.create().withId(ADDRESS_ID).withMunicipalityId(MUNICIPALITY_ID)));
		when(restaurantNumberRepository.findSequencedRestaurantNumbers(MUNICIPALITY_ID)).thenReturn(List.of("22810001", "22810003", "22819814", "2281037x"));
		when(restaurantNumberRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);

		assertThat(restaurantNumberService.createRestaurantNumber(MUNICIPALITY_ID, ADDRESS_ID)).isEqualTo("22810002");

		final var captor = ArgumentCaptor.forClass(RestaurantNumberEntity.class);
		verify(restaurantNumberRepository).saveAndFlush(captor.capture());
		assertThat(captor.getValue().getRestaurantNumber()).isEqualTo("22810002");
		assertThat(captor.getValue().getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(captor.getValue().getReported()).isFalse();
	}

	@Test
	void createRestaurantNumberRetriesWhenAnotherRequestTookTheNumber() {
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(AddressEntity.create().withId(ADDRESS_ID).withMunicipalityId(MUNICIPALITY_ID)));
		when(restaurantNumberRepository.findSequencedRestaurantNumbers(MUNICIPALITY_ID))
			.thenReturn(List.of("22810001"))
			.thenReturn(List.of("22810001", "22810002"));
		when(restaurantNumberRepository.saveAndFlush(any()))
			.thenThrow(new DataIntegrityViolationException("duplicate"))
			.thenAnswer(invocation -> invocation.getArgument(0));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);

		assertThat(restaurantNumberService.createRestaurantNumber(MUNICIPALITY_ID, ADDRESS_ID)).isEqualTo("22810003");
	}

	@Test
	void createRestaurantNumberGivesUpAfterThreeAttempts() {
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(AddressEntity.create().withId(ADDRESS_ID).withMunicipalityId(MUNICIPALITY_ID)));
		when(restaurantNumberRepository.findSequencedRestaurantNumbers(MUNICIPALITY_ID)).thenReturn(List.of());
		when(restaurantNumberRepository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);

		assertThatThrownBy(() -> restaurantNumberService.createRestaurantNumber(MUNICIPALITY_ID, ADDRESS_ID))
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

		final var method = RestaurantNumberService.class.getMethod("createRestaurantNumber", String.class, String.class);

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

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo("number-1");
		assertThat(result.getFirst().getNumber()).isEqualTo("1001");
	}

	@Test
	void getAvailableRestaurantNumbersWithNoMatch() {
		when(addressRepository.existsByIdAndMunicipalityId(ADDRESS_ID, MUNICIPALITY_ID)).thenReturn(true);
		when(restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(List.of());

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(result).isEmpty();
	}

	@Test
	void getAddressRestaurantNumbers() {
		final var occupied = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("22810001").withMunicipalityId(MUNICIPALITY_ID);
		final var vacated = RestaurantNumberEntity.create().withId("number-2").withRestaurantNumber("22810002").withMunicipalityId(MUNICIPALITY_ID);
		final var neverAssigned = RestaurantNumberEntity.create().withId("number-3").withRestaurantNumber("22810003").withMunicipalityId(MUNICIPALITY_ID);
		final var registeredAt = OffsetDateTime.of(2024, 1, 1, 10, 0, 0, 0, UTC);

		when(addressRepository.existsByIdAndMunicipalityId(ADDRESS_ID, MUNICIPALITY_ID)).thenReturn(true);
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber_Address_Id(ADDRESS_ID)).thenReturn(List.of(
			assignment(occupied, "Gamla Pub", LocalDate.of(2020, 1, 1), LocalDate.of(2023, 12, 31), ENDED, registeredAt),
			assignment(occupied, "Harrys Pub", LocalDate.of(2024, 1, 1), null, ACTIVE, registeredAt),
			assignment(vacated, "Första Baren", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 12, 31), ENDED, registeredAt),
			assignment(vacated, "Andra Baren", LocalDate.of(2022, 1, 1), LocalDate.of(2023, 6, 30), ENDED, registeredAt.plusDays(1)),
			assignment(vacated, "Utan Tidsstämpel", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 6, 30), ENDED, null)));
		when(restaurantNumberRepository.findAllByMunicipalityIdAndAddress_IdOrderByRestaurantNumber(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(List.of(occupied, vacated, neverAssigned));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getAddressRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(result)
			.extracting(AddressRestaurantNumber::getNumber, AddressRestaurantNumber::getStatus, AddressRestaurantNumber::getPremisesName, AddressRestaurantNumber::getValidFrom, AddressRestaurantNumber::getValidTo)
			.containsExactly(
				tuple("22810001", "ACTIVE", "Harrys Pub", LocalDate.of(2024, 1, 1), null),
				tuple("22810002", "AVAILABLE", "Andra Baren", LocalDate.of(2022, 1, 1), LocalDate.of(2023, 6, 30)),
				tuple("22810003", "AVAILABLE", null, null, null));
	}

	@Test
	void getAddressRestaurantNumbersShowsThePremisesOpenTodayOverAnOwnerChangeThatStartsLater() {
		final var today = LocalDate.now();
		final var takenOver = RestaurantNumberEntity.create().withId("number-1").withRestaurantNumber("22810001").withMunicipalityId(MUNICIPALITY_ID);
		final var notYetOpened = RestaurantNumberEntity.create().withId("number-2").withRestaurantNumber("22810002").withMunicipalityId(MUNICIPALITY_ID);

		when(addressRepository.existsByIdAndMunicipalityId(ADDRESS_ID, MUNICIPALITY_ID)).thenReturn(true);
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber_Address_Id(ADDRESS_ID)).thenReturn(List.of(
			assignment(takenOver, "Harrys Pub", today.minusYears(2), today.plusDays(9), ACTIVE, null),
			assignment(takenOver, "Nya Pub", today.plusDays(10), null, ACTIVE, null),
			assignment(notYetOpened, "Kaffestugan", today.plusDays(20), null, ACTIVE, null)));
		when(restaurantNumberRepository.findAllByMunicipalityIdAndAddress_IdOrderByRestaurantNumber(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(List.of(takenOver, notYetOpened));

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);
		final var result = restaurantNumberService.getAddressRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);

		assertThat(result)
			.extracting(AddressRestaurantNumber::getNumber, AddressRestaurantNumber::getStatus, AddressRestaurantNumber::getPremisesName, AddressRestaurantNumber::getValidFrom)
			.containsExactly(
				tuple("22810001", "ACTIVE", "Harrys Pub", today.minusYears(2)),
				tuple("22810002", "ACTIVE", "Kaffestugan", today.plusDays(20)));
	}

	@Test
	void getAddressRestaurantNumbersWithUnknownAddress() {
		when(addressRepository.existsByIdAndMunicipalityId(ADDRESS_ID, MUNICIPALITY_ID)).thenReturn(false);

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);

		assertThatThrownBy(() -> restaurantNumberService.getAddressRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Address " + ADDRESS_ID + " not found")
			.extracting("status").isEqualTo(NOT_FOUND);
		verifyNoInteractions(restaurantNumberRepository, restaurantNumberAssignmentRepository);
	}

	private static RestaurantNumberAssignmentEntity assignment(final RestaurantNumberEntity restaurantNumber, final String premisesName, final LocalDate validFrom, final LocalDate validTo,
		final AssignmentStatus status, final OffsetDateTime created) {
		return RestaurantNumberAssignmentEntity.create()
			.withRestaurantNumber(restaurantNumber)
			.withPremisesName(premisesName)
			.withValidFrom(validFrom)
			.withValidTo(validTo)
			.withStatus(status)
			.withCreated(created);
	}

	@Test
	void getAvailableRestaurantNumbersWithUnknownAddress() {
		when(addressRepository.existsByIdAndMunicipalityId(ADDRESS_ID, MUNICIPALITY_ID)).thenReturn(false);

		final var restaurantNumberService = new RestaurantNumberService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, restaurantNumberMapper);

		assertThatThrownBy(() -> restaurantNumberService.getAvailableRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Address " + ADDRESS_ID + " not found")
			.extracting("status").isEqualTo(NOT_FOUND);
		verifyNoInteractions(restaurantNumberRepository);
	}
}
