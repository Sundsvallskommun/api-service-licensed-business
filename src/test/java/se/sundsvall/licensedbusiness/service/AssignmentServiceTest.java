package se.sundsvall.licensedbusiness.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.AssignmentCreateRequest;
import se.sundsvall.licensedbusiness.api.model.AssignmentUpdateRequest;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.LicenseHolderRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberAssignmentRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.service.mapper.AddressMapper;
import se.sundsvall.licensedbusiness.service.mapper.AssignmentMapper;
import se.sundsvall.licensedbusiness.service.mapper.LicenseHolderMapper;
import se.sundsvall.licensedbusiness.service.mapper.RestaurantNumberMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ACTIVE;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ENDED;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String RESTAURANT_NUMBER = "22813670";
	private static final String RESTAURANT_NUMBER_ID = "rn-1";
	private static final String ADDRESS_ID = "address-1";
	private static final String ASSIGNMENT_ID = "assignment-1";
	private static final String ORG_NUMBER = "556612-4144";

	@Mock
	private RestaurantNumberRepository restaurantNumberRepository;

	@Mock
	private RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private LicenseHolderRepository licenseHolderRepository;

	private final AssignmentMapper assignmentMapper = new AssignmentMapper(new RestaurantNumberMapper(), new AddressMapper(), new LicenseHolderMapper());

	private AssignmentService assignmentService() {
		return new AssignmentService(restaurantNumberRepository, restaurantNumberAssignmentRepository, addressRepository, licenseHolderRepository, assignmentMapper);
	}

	private static RestaurantNumberEntity restaurantNumberEntity() {
		return RestaurantNumberEntity.create().withId(RESTAURANT_NUMBER_ID).withRestaurantNumber(RESTAURANT_NUMBER).withMunicipalityId(MUNICIPALITY_ID);
	}

	private static AddressEntity addressEntity() {
		return AddressEntity.create().withId(ADDRESS_ID).withStreetAddress("Storgatan 1").withMunicipalityId(MUNICIPALITY_ID);
	}

	private static AssignmentCreateRequest createRequest() {
		return AssignmentCreateRequest.create()
			.withRestaurantNumberId(RESTAURANT_NUMBER_ID)
			.withAddressId(ADDRESS_ID)
			.withOrgNumber(ORG_NUMBER)
			.withHolderName("Restaurang i Sundsvall AB")
			.withPremisesName("Harrys Pub")
			.withValidFrom(LocalDate.of(2026, 1, 1));
	}

	@Test
	void getLatestAssignment() {
		final var restaurantNumber = restaurantNumberEntity();
		final var entity = RestaurantNumberAssignmentEntity.create()
			.withId(ASSIGNMENT_ID)
			.withRestaurantNumber(restaurantNumber)
			.withAddress(addressEntity())
			.withLicenseHolder(LicenseHolderEntity.create().withId("holder-1").withOrgNumber(ORG_NUMBER))
			.withHolderName("Restaurang i Sundsvall AB")
			.withValidFrom(LocalDate.of(2024, 1, 1))
			.withStatus(ACTIVE);
		when(restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId(RESTAURANT_NUMBER, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(restaurantNumberAssignmentRepository.findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(restaurantNumber)).thenReturn(Optional.of(entity));

		final var result = assignmentService().getLatestAssignment(MUNICIPALITY_ID, RESTAURANT_NUMBER);

		assertThat(result.getId()).isEqualTo(ASSIGNMENT_ID);
		assertThat(result.getAddress().getStreetAddress()).isEqualTo("Storgatan 1");
		assertThat(result.getLicenseHolder().getOrgNumber()).isEqualTo(ORG_NUMBER);
		assertThat(result.getRestaurantNumber().getNumber()).isEqualTo(RESTAURANT_NUMBER);
	}

	@Test
	void getLatestAssignmentWithUnknownRestaurantNumber() {
		when(restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId(RESTAURANT_NUMBER, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		final var assignmentService = assignmentService();

		assertThatThrownBy(() -> assignmentService.getLatestAssignment(MUNICIPALITY_ID, RESTAURANT_NUMBER))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Restaurant number " + RESTAURANT_NUMBER + " not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void getLatestAssignmentWithoutAnyAssignment() {
		final var restaurantNumber = restaurantNumberEntity();
		when(restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId(RESTAURANT_NUMBER, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(restaurantNumberAssignmentRepository.findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(restaurantNumber)).thenReturn(Optional.empty());

		final var assignmentService = assignmentService();

		assertThatThrownBy(() -> assignmentService.getLatestAssignment(MUNICIPALITY_ID, RESTAURANT_NUMBER))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("No assignment found for restaurant number " + RESTAURANT_NUMBER)
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void getAssignment() {
		final var entity = RestaurantNumberAssignmentEntity.create().withId(ASSIGNMENT_ID).withRestaurantNumber(restaurantNumberEntity());
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));

		final var result = assignmentService().getAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID);

		assertThat(result.getId()).isEqualTo(ASSIGNMENT_ID);
	}

	@Test
	void getAssignmentNotFound() {
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		final var assignmentService = assignmentService();

		assertThatThrownBy(() -> assignmentService.getAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Assignment " + ASSIGNMENT_ID + " not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void createAssignmentReusesAnExistingLicenseHolder() {
		final var restaurantNumber = restaurantNumberEntity();
		final var licenseHolder = LicenseHolderEntity.create().withId("holder-1").withOrgNumber(ORG_NUMBER).withName("Gammalt namn");
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(licenseHolder));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of());
		when(restaurantNumberAssignmentRepository.save(any())).thenAnswer(invocation -> ((RestaurantNumberAssignmentEntity) invocation.getArgument(0)).withId(ASSIGNMENT_ID));

		final var result = assignmentService().createAssignment(MUNICIPALITY_ID, createRequest());

		assertThat(result).isEqualTo(ASSIGNMENT_ID);
		verify(licenseHolderRepository, never()).save(any());

		final var captor = ArgumentCaptor.forClass(RestaurantNumberAssignmentEntity.class);
		verify(restaurantNumberAssignmentRepository).save(captor.capture());
		assertThat(captor.getValue().getLicenseHolder()).isSameAs(licenseHolder);
		assertThat(captor.getValue().getHolderName()).isEqualTo("Restaurang i Sundsvall AB");
		assertThat(captor.getValue().getPremisesName()).isEqualTo("Harrys Pub");
		assertThat(captor.getValue().getStatus()).isEqualTo(ACTIVE);
	}

	@Test
	void createAssignmentCreatesAnUnknownLicenseHolder() {
		final var restaurantNumber = restaurantNumberEntity();
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.empty());
		when(licenseHolderRepository.save(any())).thenAnswer(invocation -> ((LicenseHolderEntity) invocation.getArgument(0)).withId("holder-new"));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of());
		when(restaurantNumberAssignmentRepository.save(any())).thenAnswer(invocation -> ((RestaurantNumberAssignmentEntity) invocation.getArgument(0)).withId(ASSIGNMENT_ID));

		assignmentService().createAssignment(MUNICIPALITY_ID, createRequest());

		final var captor = ArgumentCaptor.forClass(LicenseHolderEntity.class);
		verify(licenseHolderRepository).save(captor.capture());
		assertThat(captor.getValue().getOrgNumber()).isEqualTo(ORG_NUMBER);
		assertThat(captor.getValue().getName()).isEqualTo("Restaurang i Sundsvall AB");
	}

	@Test
	void createAssignmentEndsTheCurrentAssignmentTheDayBefore() {
		final var restaurantNumber = restaurantNumberEntity();
		final var current = RestaurantNumberAssignmentEntity.create().withId("assignment-old").withValidFrom(LocalDate.of(2020, 1, 1)).withStatus(ACTIVE);
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(LicenseHolderEntity.create().withId("holder-1")));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of(current));
		when(restaurantNumberAssignmentRepository.save(any())).thenAnswer(invocation -> ((RestaurantNumberAssignmentEntity) invocation.getArgument(0)).withId(ASSIGNMENT_ID));

		assignmentService().createAssignment(MUNICIPALITY_ID, createRequest());

		assertThat(current.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
		assertThat(current.getStatus()).isEqualTo(ENDED);
	}

	@Test
	void createAssignmentRejectsAnAssignmentStartingOnOrAfterTheNewOne() {
		final var restaurantNumber = restaurantNumberEntity();
		final var current = RestaurantNumberAssignmentEntity.create().withId("assignment-old").withValidFrom(LocalDate.of(2026, 1, 1)).withStatus(ACTIVE);
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(LicenseHolderEntity.create().withId("holder-1")));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of(current));

		final var assignmentService = assignmentService();
		final var request = createRequest();

		assertThatThrownBy(() -> assignmentService.createAssignment(MUNICIPALITY_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("overlaps assignment assignment-old, which runs from 2026-01-01")
			.extracting("status").isEqualTo(BAD_REQUEST);
	}

	@Test
	void createAssignmentDerivesEndedStatusFromAPastValidTo() {
		final var restaurantNumber = restaurantNumberEntity();
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(LicenseHolderEntity.create().withId("holder-1")));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of());
		when(restaurantNumberAssignmentRepository.save(any())).thenAnswer(invocation -> ((RestaurantNumberAssignmentEntity) invocation.getArgument(0)).withId(ASSIGNMENT_ID));

		final var request = createRequest().withValidFrom(LocalDate.of(2020, 1, 1)).withValidTo(LocalDate.of(2020, 12, 31));
		assignmentService().createAssignment(MUNICIPALITY_ID, request);

		final var captor = ArgumentCaptor.forClass(RestaurantNumberAssignmentEntity.class);
		verify(restaurantNumberAssignmentRepository).save(captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo(ENDED);
	}

	@Test
	void createAssignmentWithValidToBeforeValidFrom() {
		final var assignmentService = assignmentService();
		final var request = createRequest().withValidTo(LocalDate.of(2025, 12, 31));

		assertThatThrownBy(() -> assignmentService.createAssignment(MUNICIPALITY_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("validTo must not be before validFrom")
			.extracting("status").isEqualTo(BAD_REQUEST);
	}

	@Test
	void createAssignmentWithUnknownRestaurantNumber() {
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		final var assignmentService = assignmentService();
		final var request = createRequest();

		assertThatThrownBy(() -> assignmentService.createAssignment(MUNICIPALITY_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Restaurant number with ID " + RESTAURANT_NUMBER_ID + " not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void createAssignmentWithUnknownAddress() {
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumberEntity()));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.empty());

		final var assignmentService = assignmentService();
		final var request = createRequest();

		assertThatThrownBy(() -> assignmentService.createAssignment(MUNICIPALITY_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Address " + ADDRESS_ID + " not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void createAssignmentWithAddressInAnotherMunicipality() {
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumberEntity()));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(AddressEntity.create().withId(ADDRESS_ID).withMunicipalityId("2260")));

		final var assignmentService = assignmentService();
		final var request = createRequest();

		assertThatThrownBy(() -> assignmentService.createAssignment(MUNICIPALITY_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Address " + ADDRESS_ID + " not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void updateAssignmentEndsAnAssignmentAndRecalculatesStatus() {
		final var entity = RestaurantNumberAssignmentEntity.create()
			.withId(ASSIGNMENT_ID)
			.withRestaurantNumber(restaurantNumberEntity())
			.withValidFrom(LocalDate.of(2020, 1, 1))
			.withStatus(ACTIVE);
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));
		when(restaurantNumberAssignmentRepository.saveAndFlush(entity)).thenReturn(entity);

		final var result = assignmentService().updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, AssignmentUpdateRequest.create().withValidTo(LocalDate.of(2020, 12, 31)));

		assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
		assertThat(result.getStatus()).isEqualTo(ENDED.name());
	}

	@Test
	void updateAssignmentReactivatesWhenValidToIsMovedIntoTheFuture() {
		final var entity = RestaurantNumberAssignmentEntity.create()
			.withId(ASSIGNMENT_ID)
			.withRestaurantNumber(restaurantNumberEntity())
			.withValidFrom(LocalDate.of(2020, 1, 1))
			.withValidTo(LocalDate.of(2020, 12, 31))
			.withStatus(ENDED);
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));
		when(restaurantNumberAssignmentRepository.saveAndFlush(entity)).thenReturn(entity);

		final var future = LocalDate.now().plusYears(1);
		final var result = assignmentService().updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, AssignmentUpdateRequest.create().withValidTo(future));

		assertThat(result.getValidTo()).isEqualTo(future);
		assertThat(result.getStatus()).isEqualTo(ACTIVE.name());
	}

	@Test
	void updateAssignmentLeavesOmittedFieldsUnchanged() {
		final var entity = RestaurantNumberAssignmentEntity.create()
			.withId(ASSIGNMENT_ID)
			.withRestaurantNumber(restaurantNumberEntity())
			.withHolderName("Gammalt namn")
			.withPremisesName("Gammal lokal")
			.withValidFrom(LocalDate.of(2020, 1, 1))
			.withStatus(ACTIVE);
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));
		when(restaurantNumberAssignmentRepository.saveAndFlush(entity)).thenReturn(entity);

		final var result = assignmentService().updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, AssignmentUpdateRequest.create().withPremisesName("Ny lokal"));

		assertThat(result.getPremisesName()).isEqualTo("Ny lokal");
		assertThat(result.getHolderName()).isEqualTo("Gammalt namn");
		assertThat(result.getValidTo()).isNull();
		assertThat(result.getStatus()).isEqualTo(ACTIVE.name());
	}

	@Test
	void updateAssignmentWithValidToBeforeValidFrom() {
		final var entity = RestaurantNumberAssignmentEntity.create()
			.withId(ASSIGNMENT_ID)
			.withRestaurantNumber(restaurantNumberEntity())
			.withValidFrom(LocalDate.of(2020, 1, 1))
			.withStatus(ACTIVE);
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));

		final var assignmentService = assignmentService();
		final var request = AssignmentUpdateRequest.create().withValidTo(LocalDate.of(2019, 12, 31));

		assertThatThrownBy(() -> assignmentService.updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("validTo must not be before validFrom")
			.extracting("status").isEqualTo(BAD_REQUEST);
		verify(restaurantNumberAssignmentRepository, never()).saveAndFlush(any());
	}

	@Test
	void updateAssignmentWhenAnotherWriterAlreadyChangedIt() {
		final var entity = RestaurantNumberAssignmentEntity.create()
			.withId(ASSIGNMENT_ID)
			.withRestaurantNumber(restaurantNumberEntity())
			.withValidFrom(LocalDate.of(2020, 1, 1))
			.withStatus(ACTIVE);
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));
		when(restaurantNumberAssignmentRepository.saveAndFlush(entity)).thenThrow(new OptimisticLockingFailureException("conflict"));

		final var assignmentService = assignmentService();
		final var request = AssignmentUpdateRequest.create().withValidTo(LocalDate.of(2020, 12, 31));

		assertThatThrownBy(() -> assignmentService.updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Assignment " + ASSIGNMENT_ID + " was updated by someone else")
			.extracting("status").isEqualTo(CONFLICT);
	}

	@Test
	void updateAssignmentNotFound() {
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		final var assignmentService = assignmentService();
		final var request = AssignmentUpdateRequest.create().withPremisesName("Ny lokal");

		assertThatThrownBy(() -> assignmentService.updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Assignment " + ASSIGNMENT_ID + " not found")
			.extracting("status").isEqualTo(NOT_FOUND);
	}

	@Test
	void notFoundMessageSanitizesTheValueItEchoesBack() {
		final var injected = "2281\r\nFAKE LOG LINE";
		when(restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId(injected, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		final var assignmentService = assignmentService();

		assertThatThrownBy(() -> assignmentService.getLatestAssignment(MUNICIPALITY_ID, injected))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Restaurant number 2281 FAKE LOG LINE not found")
			.hasMessageNotContaining("\n")
			.hasMessageNotContaining("\r");
	}

	@Test
	void createAssignmentLeavesAnAssignmentThatAlreadyEndsEarlierAlone() {
		final var restaurantNumber = restaurantNumberEntity();
		final var current = RestaurantNumberAssignmentEntity.create()
			.withId("assignment-old")
			.withValidFrom(LocalDate.of(2026, 1, 1))
			.withValidTo(LocalDate.of(2026, 3, 31))
			.withStatus(ACTIVE);
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(LicenseHolderEntity.create().withId("holder-1")));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of(current));
		when(restaurantNumberAssignmentRepository.save(any())).thenAnswer(invocation -> ((RestaurantNumberAssignmentEntity) invocation.getArgument(0)).withId(ASSIGNMENT_ID));

		assignmentService().createAssignment(MUNICIPALITY_ID, createRequest().withValidFrom(LocalDate.of(2026, 9, 1)));

		assertThat(current.getValidTo()).isEqualTo(LocalDate.of(2026, 3, 31));
		verify(restaurantNumberAssignmentRepository, times(1)).save(any());
	}

	@Test
	void createAssignmentKeepsAShortenedAssignmentActiveWhileItStillHasDaysLeft() {
		final var restaurantNumber = restaurantNumberEntity();
		final var current = RestaurantNumberAssignmentEntity.create()
			.withId("assignment-old")
			.withValidFrom(LocalDate.of(2020, 1, 1))
			.withStatus(ACTIVE);
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(LicenseHolderEntity.create().withId("holder-1")));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of(current));
		when(restaurantNumberAssignmentRepository.save(any())).thenAnswer(invocation -> ((RestaurantNumberAssignmentEntity) invocation.getArgument(0)).withId(ASSIGNMENT_ID));

		final var futureStart = LocalDate.now().plusYears(1);
		assignmentService().createAssignment(MUNICIPALITY_ID, createRequest().withValidFrom(futureStart));

		assertThat(current.getValidTo()).isEqualTo(futureStart.minusDays(1));
		assertThat(current.getStatus()).isEqualTo(ACTIVE);
	}

	@Test
	void createAssignmentRejectsABackdatedPeriodInsteadOfEndingTheRunningAssignment() {
		final var restaurantNumber = restaurantNumberEntity();
		final var running = RestaurantNumberAssignmentEntity.create()
			.withId("assignment-running")
			.withValidFrom(LocalDate.of(2019, 1, 1))
			.withStatus(ACTIVE);
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(LicenseHolderEntity.create().withId("holder-1")));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of(running));

		final var assignmentService = assignmentService();
		final var request = createRequest().withValidFrom(LocalDate.of(2020, 1, 1)).withValidTo(LocalDate.of(2020, 12, 31));

		assertThatThrownBy(() -> assignmentService.createAssignment(MUNICIPALITY_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("overlaps assignment assignment-running")
			.extracting("status").isEqualTo(BAD_REQUEST);

		assertThat(running.getValidTo()).isNull();
		assertThat(running.getStatus()).isEqualTo(ACTIVE);
		verify(restaurantNumberAssignmentRepository, never()).save(any());
	}

	@Test
	void createAssignmentRejectsAPeriodThatOverlapsAnEndedAssignment() {
		final var restaurantNumber = restaurantNumberEntity();
		final var historic = RestaurantNumberAssignmentEntity.create()
			.withId("assignment-historic")
			.withValidFrom(LocalDate.of(2005, 1, 1))
			.withValidTo(LocalDate.of(2005, 12, 31))
			.withStatus(ENDED);
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(LicenseHolderEntity.create().withId("holder-1")));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of(historic));

		final var assignmentService = assignmentService();
		final var request = createRequest().withValidFrom(LocalDate.of(2005, 6, 1)).withValidTo(LocalDate.of(2005, 8, 31));

		assertThatThrownBy(() -> assignmentService.createAssignment(MUNICIPALITY_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("overlaps assignment assignment-historic")
			.extracting("status").isEqualTo(BAD_REQUEST);
	}

	@Test
	void createAssignmentAcceptsAHistoricPeriodThatFitsBetweenExistingOnes() {
		final var restaurantNumber = restaurantNumberEntity();
		final var historic = RestaurantNumberAssignmentEntity.create()
			.withId("assignment-historic")
			.withValidFrom(LocalDate.of(2005, 1, 1))
			.withValidTo(LocalDate.of(2005, 12, 31))
			.withStatus(ENDED);
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(LicenseHolderEntity.create().withId("holder-1")));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of(historic));
		when(restaurantNumberAssignmentRepository.save(any())).thenAnswer(invocation -> ((RestaurantNumberAssignmentEntity) invocation.getArgument(0)).withId(ASSIGNMENT_ID));

		final var request = createRequest().withValidFrom(LocalDate.of(2006, 1, 1)).withValidTo(LocalDate.of(2006, 12, 31));
		assignmentService().createAssignment(MUNICIPALITY_ID, request);

		assertThat(historic.getValidTo()).isEqualTo(LocalDate.of(2005, 12, 31));
		verify(restaurantNumberAssignmentRepository, times(1)).save(any());
	}

	@Test
	void createAssignmentNormalizesTheOrgNumberBeforeLookingUpTheLicenseHolder() {
		final var restaurantNumber = restaurantNumberEntity();
		final var licenseHolder = LicenseHolderEntity.create().withId("holder-1").withOrgNumber(ORG_NUMBER);
		when(restaurantNumberRepository.findByIdAndMunicipalityId(RESTAURANT_NUMBER_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(restaurantNumber));
		when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(addressEntity()));
		when(licenseHolderRepository.findByOrgNumber(ORG_NUMBER)).thenReturn(Optional.of(licenseHolder));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumber(restaurantNumber)).thenReturn(List.of());
		when(restaurantNumberAssignmentRepository.save(any())).thenAnswer(invocation -> ((RestaurantNumberAssignmentEntity) invocation.getArgument(0)).withId(ASSIGNMENT_ID));

		assignmentService().createAssignment(MUNICIPALITY_ID, createRequest().withOrgNumber("5566124144"));

		verify(licenseHolderRepository).findByOrgNumber(ORG_NUMBER);
		verify(licenseHolderRepository, never()).save(any());
	}

	@Test
	void updateAssignmentRejectsReopeningWhenTheNumberAlreadyHasAnActiveAssignment() {
		final var restaurantNumber = restaurantNumberEntity();
		final var entity = RestaurantNumberAssignmentEntity.create()
			.withId(ASSIGNMENT_ID)
			.withRestaurantNumber(restaurantNumber)
			.withValidFrom(LocalDate.of(2020, 1, 1))
			.withValidTo(LocalDate.of(2020, 12, 31))
			.withStatus(ENDED);
		final var other = RestaurantNumberAssignmentEntity.create().withId("assignment-current").withStatus(ACTIVE);
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumberAndStatus(restaurantNumber, ACTIVE)).thenReturn(List.of(other));

		final var assignmentService = assignmentService();
		final var request = AssignmentUpdateRequest.create().withValidTo(LocalDate.now().plusYears(1));

		assertThatThrownBy(() -> assignmentService.updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, request))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Restaurant number %s already has an active assignment with ID assignment-current".formatted(RESTAURANT_NUMBER))
			.extracting("status").isEqualTo(BAD_REQUEST);
		verify(restaurantNumberAssignmentRepository, never()).save(any());
	}

	@Test
	void updateAssignmentDoesNotCountTheAssignmentBeingUpdatedAsAConflict() {
		final var restaurantNumber = restaurantNumberEntity();
		final var entity = RestaurantNumberAssignmentEntity.create()
			.withId(ASSIGNMENT_ID)
			.withRestaurantNumber(restaurantNumber)
			.withValidFrom(LocalDate.of(2020, 1, 1))
			.withStatus(ACTIVE);
		when(restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(ASSIGNMENT_ID, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));
		when(restaurantNumberAssignmentRepository.findAllByRestaurantNumberAndStatus(restaurantNumber, ACTIVE)).thenReturn(List.of(entity));
		when(restaurantNumberAssignmentRepository.saveAndFlush(entity)).thenReturn(entity);

		final var future = LocalDate.now().plusYears(1);
		final var result = assignmentService().updateAssignment(MUNICIPALITY_ID, ASSIGNMENT_ID, AssignmentUpdateRequest.create().withValidTo(future));

		assertThat(result.getValidTo()).isEqualTo(future);
		assertThat(result.getStatus()).isEqualTo(ACTIVE.name());
	}
}
