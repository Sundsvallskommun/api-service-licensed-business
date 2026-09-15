package se.sundsvall.licensedbusiness.service;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.Assignment;
import se.sundsvall.licensedbusiness.api.model.AssignmentCreateRequest;
import se.sundsvall.licensedbusiness.api.model.AssignmentUpdateRequest;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.LicenseHolderRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberAssignmentRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.service.mapper.AssignmentMapper;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ACTIVE;
import static se.sundsvall.licensedbusiness.service.AssignmentStatusResolver.resolveStatus;

@Service
public class AssignmentService {

	private final RestaurantNumberRepository restaurantNumberRepository;

	private final RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	private final AddressRepository addressRepository;

	private final LicenseHolderRepository licenseHolderRepository;

	private final AssignmentMapper assignmentMapper;

	public AssignmentService(final RestaurantNumberRepository restaurantNumberRepository, final RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository,
		final AddressRepository addressRepository, final LicenseHolderRepository licenseHolderRepository, final AssignmentMapper assignmentMapper) {
		this.restaurantNumberRepository = restaurantNumberRepository;
		this.restaurantNumberAssignmentRepository = restaurantNumberAssignmentRepository;
		this.addressRepository = addressRepository;
		this.licenseHolderRepository = licenseHolderRepository;
		this.assignmentMapper = assignmentMapper;
	}

	public Assignment getLatestAssignment(final String municipalityId, final String restaurantNumber) {
		final var restaurantNumberEntity = restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId(restaurantNumber, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Restaurant number %s not found".formatted(sanitizeForLogging(restaurantNumber))));

		return restaurantNumberAssignmentRepository.findFirstByRestaurantNumberOrderByValidFromDescCreatedDesc(restaurantNumberEntity)
			.map(assignmentMapper::toAssignment)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No assignment found for restaurant number %s".formatted(sanitizeForLogging(restaurantNumber))));
	}

	public Assignment getAssignment(final String municipalityId, final String assignmentId) {
		return restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(assignmentId, municipalityId)
			.map(assignmentMapper::toAssignment)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Assignment %s not found".formatted(sanitizeForLogging(assignmentId))));
	}

	@Transactional
	public String createAssignment(final String municipalityId, final AssignmentCreateRequest request) {
		validateValidToNotBeforeValidFrom(request.getValidFrom(), request.getValidTo());

		final var restaurantNumber = restaurantNumberRepository.findByIdAndMunicipalityId(request.getRestaurantNumberId(), municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Restaurant number with ID %s not found".formatted(sanitizeForLogging(request.getRestaurantNumberId()))));
		final var address = addressRepository.findById(request.getAddressId())
			.filter(entity -> municipalityId.equals(entity.getMunicipalityId()))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Address %s not found".formatted(sanitizeForLogging(request.getAddressId()))));

		final var licenseHolder = licenseHolderRepository.findByOrgNumber(request.getOrgNumber())
			.orElseGet(() -> licenseHolderRepository.save(LicenseHolderEntity.create()
				.withOrgNumber(request.getOrgNumber())
				.withName(request.getHolderName())));

		endCurrentAssignments(restaurantNumber, request.getValidFrom());

		return restaurantNumberAssignmentRepository.save(RestaurantNumberAssignmentEntity.create()
			.withRestaurantNumber(restaurantNumber)
			.withAddress(address)
			.withLicenseHolder(licenseHolder)
			.withHolderName(request.getHolderName())
			.withPremisesName(request.getPremisesName())
			.withValidFrom(request.getValidFrom())
			.withValidTo(request.getValidTo())
			.withStatus(resolveStatus(request.getValidTo())))
			.getId();
	}

	@Transactional
	public Assignment updateAssignment(final String municipalityId, final String assignmentId, final AssignmentUpdateRequest request) {
		final var entity = restaurantNumberAssignmentRepository.findByIdAndRestaurantNumber_MunicipalityId(assignmentId, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Assignment %s not found".formatted(sanitizeForLogging(assignmentId))));

		Optional.ofNullable(request.getValidTo()).ifPresent(entity::setValidTo);
		Optional.ofNullable(request.getPremisesName()).ifPresent(entity::setPremisesName);
		Optional.ofNullable(request.getHolderName()).ifPresent(entity::setHolderName);

		validateValidToNotBeforeValidFrom(entity.getValidFrom(), entity.getValidTo());

		final var status = resolveStatus(entity.getValidTo());
		if (status == ACTIVE) {
			validateNoOtherActiveAssignment(entity);
		}
		entity.setStatus(status);

		return assignmentMapper.toAssignment(restaurantNumberAssignmentRepository.save(entity));
	}

	private void endCurrentAssignments(final RestaurantNumberEntity restaurantNumber, final LocalDate newValidFrom) {
		final var lastValidDay = newValidFrom.minusDays(1);

		restaurantNumberAssignmentRepository.findAllByRestaurantNumberAndStatus(restaurantNumber, ACTIVE)
			.forEach(current -> {
				if (!current.getValidFrom().isBefore(newValidFrom)) {
					throw Problem.valueOf(BAD_REQUEST, "The restaurant number has an active assignment starting on " + current.getValidFrom() + ", which is not before " + newValidFrom);
				}
				if (runsPast(current, lastValidDay)) {
					current.setValidTo(lastValidDay);
					current.setStatus(resolveStatus(lastValidDay));
					restaurantNumberAssignmentRepository.save(current);
				}
			});
	}

	private static boolean runsPast(final RestaurantNumberAssignmentEntity assignment, final LocalDate day) {
		return (assignment.getValidTo() == null) || assignment.getValidTo().isAfter(day);
	}

	private void validateNoOtherActiveAssignment(final RestaurantNumberAssignmentEntity assignment) {
		restaurantNumberAssignmentRepository.findAllByRestaurantNumberAndStatus(assignment.getRestaurantNumber(), ACTIVE).stream()
			.filter(other -> !other.getId().equals(assignment.getId()))
			.findFirst()
			.ifPresent(other -> {
				throw Problem.valueOf(BAD_REQUEST, "Restaurant number %s already has an active assignment with ID %s".formatted(
					sanitizeForLogging(assignment.getRestaurantNumber().getRestaurantNumber()), sanitizeForLogging(other.getId())));
			});
	}

	private static void validateValidToNotBeforeValidFrom(final LocalDate validFrom, final LocalDate validTo) {
		if (validTo != null && validTo.isBefore(validFrom)) {
			throw Problem.valueOf(BAD_REQUEST, "validTo must not be before validFrom");
		}
	}
}
