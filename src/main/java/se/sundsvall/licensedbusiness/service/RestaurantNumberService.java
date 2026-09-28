package se.sundsvall.licensedbusiness.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.AddressRestaurantNumber;
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberAssignmentRepository;
import se.sundsvall.licensedbusiness.integration.db.dao.RestaurantNumberRepository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberAssignmentEntity;
import se.sundsvall.licensedbusiness.integration.db.model.RestaurantNumberEntity;
import se.sundsvall.licensedbusiness.service.mapper.RestaurantNumberMapper;

import static java.util.Collections.emptyList;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ACTIVE;
import static se.sundsvall.licensedbusiness.service.TextSanitizer.sanitize;

@Service
public class RestaurantNumberService {

	private static final int SEQUENCE_DIGITS = 4;
	private static final int MAX_SEQUENCE = 9999;
	private static final int MAX_ATTEMPTS = 3;
	private static final String SEQUENCE_PATTERN = "\\d{%d}".formatted(SEQUENCE_DIGITS);
	private static final String SEQUENCE_FORMAT = "%%0%dd".formatted(SEQUENCE_DIGITS);
	private static final String STATUS_ACTIVE = "ACTIVE";
	private static final String STATUS_AVAILABLE = "AVAILABLE";
	private static final String ADDRESS_NOT_FOUND = "Address %s not found";
	private static final Comparator<RestaurantNumberAssignmentEntity> BY_VALID_FROM_THEN_CREATED = Comparator
		.comparing(RestaurantNumberAssignmentEntity::getValidFrom)
		.thenComparing(RestaurantNumberAssignmentEntity::getCreated, nullsFirst(naturalOrder()));

	private final RestaurantNumberRepository restaurantNumberRepository;

	private final RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository;

	private final AddressRepository addressRepository;

	private final RestaurantNumberMapper restaurantNumberMapper;

	public RestaurantNumberService(final RestaurantNumberRepository restaurantNumberRepository, final RestaurantNumberAssignmentRepository restaurantNumberAssignmentRepository,
		final AddressRepository addressRepository, final RestaurantNumberMapper restaurantNumberMapper) {
		this.restaurantNumberRepository = restaurantNumberRepository;
		this.restaurantNumberAssignmentRepository = restaurantNumberAssignmentRepository;
		this.addressRepository = addressRepository;
		this.restaurantNumberMapper = restaurantNumberMapper;
	}

	public RestaurantNumber getRestaurantNumber(final String municipalityId, final String restaurantNumber) {
		return restaurantNumberRepository.findByRestaurantNumberAndMunicipalityId(restaurantNumber, municipalityId)
			.map(restaurantNumberMapper::toRestaurantNumber)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Restaurant number %s not found".formatted(sanitize(restaurantNumber))));
	}

	public String createRestaurantNumber(final String municipalityId, final String addressId) {
		final var address = addressRepository.findById(addressId)
			.filter(entity -> municipalityId.equals(entity.getMunicipalityId()))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ADDRESS_NOT_FOUND.formatted(sanitize(addressId))));

		for (var attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
			final var allocated = allocateNextFreeRestaurantNumber(municipalityId, address);
			if (allocated.isPresent()) {
				return allocated.get();
			}
		}

		throw Problem.valueOf(CONFLICT, "Could not allocate a restaurant number for municipality %s, please try again".formatted(sanitize(municipalityId)));
	}

	private Optional<String> allocateNextFreeRestaurantNumber(final String municipalityId, final AddressEntity address) {
		final var restaurantNumber = nextFreeRestaurantNumber(municipalityId);
		try {
			restaurantNumberRepository.saveAndFlush(RestaurantNumberEntity.create()
				.withRestaurantNumber(restaurantNumber)
				.withMunicipalityId(municipalityId)
				.withAddress(address)
				.withReported(false));

			return Optional.of(restaurantNumber);
		} catch (final DataIntegrityViolationException numberTakenByConcurrentRequest) {
			return Optional.empty();
		}
	}

	private String nextFreeRestaurantNumber(final String municipalityId) {
		final var used = usedSequences(municipalityId);

		return IntStream.rangeClosed(1, MAX_SEQUENCE)
			.filter(sequence -> !used.contains(sequence))
			.mapToObj(sequence -> municipalityId + SEQUENCE_FORMAT.formatted(sequence))
			.findFirst()
			.orElseThrow(() -> Problem.valueOf(CONFLICT, "No free restaurant number is available for municipality %s".formatted(sanitize(municipalityId))));
	}

	private Set<Integer> usedSequences(final String municipalityId) {
		return restaurantNumberRepository.findSequencedRestaurantNumbers(municipalityId).stream()
			.map(restaurantNumber -> restaurantNumber.substring(municipalityId.length()))
			.filter(sequence -> sequence.matches(SEQUENCE_PATTERN))
			.map(Integer::parseInt)
			.collect(toSet());
	}

	public List<RestaurantNumber> getAvailableRestaurantNumbers(final String municipalityId, final String addressId) {
		if (!addressRepository.existsByIdAndMunicipalityId(addressId, municipalityId)) {
			throw Problem.valueOf(NOT_FOUND, ADDRESS_NOT_FOUND.formatted(sanitize(addressId)));
		}

		final var entities = restaurantNumberRepository.findAvailableByMunicipalityIdAndAddressId(municipalityId, addressId);
		return restaurantNumberMapper.toRestaurantNumbers(entities);
	}

	@Transactional(readOnly = true)
	public List<AddressRestaurantNumber> getAddressRestaurantNumbers(final String municipalityId, final String addressId) {
		if (!addressRepository.existsByIdAndMunicipalityId(addressId, municipalityId)) {
			throw Problem.valueOf(NOT_FOUND, ADDRESS_NOT_FOUND.formatted(sanitize(addressId)));
		}

		final var assignmentsPerNumber = restaurantNumberAssignmentRepository.findAllByRestaurantNumber_Address_Id(addressId).stream()
			.collect(groupingBy(assignment -> assignment.getRestaurantNumber().getId()));
		final var startedFirst = startedFirst(LocalDate.now(ZoneId.systemDefault()));

		return restaurantNumberRepository.findAllByMunicipalityIdAndAddress_IdOrderByRestaurantNumber(municipalityId, addressId).stream()
			.map(restaurantNumber -> toAddressRestaurantNumber(restaurantNumber, assignmentsPerNumber.getOrDefault(restaurantNumber.getId(), emptyList()), startedFirst))
			.toList();
	}

	private AddressRestaurantNumber toAddressRestaurantNumber(final RestaurantNumberEntity restaurantNumber, final List<RestaurantNumberAssignmentEntity> assignments,
		final Comparator<RestaurantNumberAssignmentEntity> startedFirst) {
		final var shownAssignment = assignments.stream().max(startedFirst).orElse(null);
		final var status = assignments.stream().anyMatch(assignment -> assignment.getStatus() == ACTIVE) ? STATUS_ACTIVE : STATUS_AVAILABLE;

		return restaurantNumberMapper.toAddressRestaurantNumber(restaurantNumber, shownAssignment, status);
	}

	private static Comparator<RestaurantNumberAssignmentEntity> startedFirst(final LocalDate today) {
		return Comparator.comparing((final RestaurantNumberAssignmentEntity assignment) -> !assignment.getValidFrom().isAfter(today))
			.thenComparing(BY_VALID_FROM_THEN_CREATED);
	}
}
