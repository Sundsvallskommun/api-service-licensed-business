package se.sundsvall.licensedbusiness.integration.db.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class RestaurantNumberAssignmentEntityTest {

	private static final String ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String HOLDER_NAME = "Test Restaurang AB";
	private static final String PREMISES_NAME = "Harrys Pub";
	private static final LocalDate VALID_FROM = LocalDate.of(2024, 1, 1);
	private static final LocalDate VALID_TO = LocalDate.of(2024, 12, 31);
	private static final AssignmentStatus STATUS = AssignmentStatus.ACTIVE;
	private static final OffsetDateTime CREATED = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, UTC);
	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> CREATED.plusDays(SEQUENCE.incrementAndGet()), OffsetDateTime.class);
		registerValueGenerator(() -> VALID_FROM.plusDays(SEQUENCE.incrementAndGet()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(RestaurantNumberAssignmentEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("restaurantNumber", "licenseHolder"),
			hasValidBeanEqualsExcluding("restaurantNumber", "licenseHolder"),
			hasValidBeanToStringExcluding("restaurantNumber", "licenseHolder")));
	}

	@Test
	void builderTest() {
		final var restaurantNumber = new RestaurantNumberEntity();
		final var licenseHolder = new LicenseHolderEntity();
		final var assignmentEntity = RestaurantNumberAssignmentEntity.create()
			.withId(ID)
			.withRestaurantNumber(restaurantNumber)
			.withLicenseHolder(licenseHolder)
			.withHolderName(HOLDER_NAME)
			.withPremisesName(PREMISES_NAME)
			.withValidFrom(VALID_FROM)
			.withValidTo(VALID_TO)
			.withStatus(STATUS)
			.withCreated(CREATED);

		assertThat(assignmentEntity.getId()).isEqualTo(ID);
		assertThat(assignmentEntity.getRestaurantNumber()).isEqualTo(restaurantNumber);
		assertThat(assignmentEntity.getLicenseHolder()).isEqualTo(licenseHolder);
		assertThat(assignmentEntity.getHolderName()).isEqualTo(HOLDER_NAME);
		assertThat(assignmentEntity.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(assignmentEntity.getValidFrom()).isEqualTo(VALID_FROM);
		assertThat(assignmentEntity.getValidTo()).isEqualTo(VALID_TO);
		assertThat(assignmentEntity.getStatus()).isEqualTo(STATUS);
		assertThat(assignmentEntity.getCreated()).isEqualTo(CREATED);
		assertThat(assignmentEntity).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var restaurantNumber = new RestaurantNumberEntity();
		final var licenseHolder = new LicenseHolderEntity();
		final var assignmentEntity = new RestaurantNumberAssignmentEntity();
		assignmentEntity.setId(ID);
		assignmentEntity.setRestaurantNumber(restaurantNumber);
		assignmentEntity.setLicenseHolder(licenseHolder);
		assignmentEntity.setHolderName(HOLDER_NAME);
		assignmentEntity.setPremisesName(PREMISES_NAME);
		assignmentEntity.setValidFrom(VALID_FROM);
		assignmentEntity.setValidTo(VALID_TO);
		assignmentEntity.setStatus(STATUS);
		assignmentEntity.setCreated(CREATED);

		assertThat(assignmentEntity.getId()).isEqualTo(ID);
		assertThat(assignmentEntity.getRestaurantNumber()).isEqualTo(restaurantNumber);
		assertThat(assignmentEntity.getLicenseHolder()).isEqualTo(licenseHolder);
		assertThat(assignmentEntity.getHolderName()).isEqualTo(HOLDER_NAME);
		assertThat(assignmentEntity.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(assignmentEntity.getValidFrom()).isEqualTo(VALID_FROM);
		assertThat(assignmentEntity.getValidTo()).isEqualTo(VALID_TO);
		assertThat(assignmentEntity.getStatus()).isEqualTo(STATUS);
		assertThat(assignmentEntity.getCreated()).isEqualTo(CREATED);
		assertThat(assignmentEntity).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new RestaurantNumberAssignmentEntity()).hasAllNullFieldsOrProperties();
	}
}
