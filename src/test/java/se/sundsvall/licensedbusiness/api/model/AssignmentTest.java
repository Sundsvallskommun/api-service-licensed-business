package se.sundsvall.licensedbusiness.api.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class AssignmentTest {

	private static final String ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String HOLDER_NAME = "Restaurang i Sundsvall AB";
	private static final String PREMISES_NAME = "Harrys Pub";
	private static final LocalDate VALID_FROM = LocalDate.of(2024, 1, 1);
	private static final LocalDate VALID_TO = LocalDate.of(2024, 12, 31);
	private static final String STATUS = "ACTIVE";
	private static final OffsetDateTime CREATED = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, UTC);
	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> CREATED.plusDays(SEQUENCE.incrementAndGet()), OffsetDateTime.class);
		registerValueGenerator(() -> VALID_FROM.plusDays(SEQUENCE.incrementAndGet()), LocalDate.class);
		registerValueGenerator(() -> RestaurantNumber.create().withId("number-" + SEQUENCE.incrementAndGet()), RestaurantNumber.class);
		registerValueGenerator(() -> Address.create().withId("address-" + SEQUENCE.incrementAndGet()), Address.class);
		registerValueGenerator(() -> LicenseHolder.create().withId("holder-" + SEQUENCE.incrementAndGet()), LicenseHolder.class);
	}

	@Test
	void testBean() {
		assertThat(Assignment.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var restaurantNumber = RestaurantNumber.create().withId("number-1");
		final var address = Address.create().withId("address-1");
		final var licenseHolder = LicenseHolder.create().withId("holder-1");

		final var assignment = Assignment.create()
			.withId(ID)
			.withRestaurantNumber(restaurantNumber)
			.withAddress(address)
			.withLicenseHolder(licenseHolder)
			.withHolderName(HOLDER_NAME)
			.withPremisesName(PREMISES_NAME)
			.withValidFrom(VALID_FROM)
			.withValidTo(VALID_TO)
			.withStatus(STATUS)
			.withCreated(CREATED);

		assertThat(assignment.getId()).isEqualTo(ID);
		assertThat(assignment.getRestaurantNumber()).isEqualTo(restaurantNumber);
		assertThat(assignment.getAddress()).isEqualTo(address);
		assertThat(assignment.getLicenseHolder()).isEqualTo(licenseHolder);
		assertThat(assignment.getHolderName()).isEqualTo(HOLDER_NAME);
		assertThat(assignment.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(assignment.getValidFrom()).isEqualTo(VALID_FROM);
		assertThat(assignment.getValidTo()).isEqualTo(VALID_TO);
		assertThat(assignment.getStatus()).isEqualTo(STATUS);
		assertThat(assignment.getCreated()).isEqualTo(CREATED);
		assertThat(assignment).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var restaurantNumber = RestaurantNumber.create().withId("number-1");
		final var address = Address.create().withId("address-1");
		final var licenseHolder = LicenseHolder.create().withId("holder-1");

		final var assignment = new Assignment();
		assignment.setId(ID);
		assignment.setRestaurantNumber(restaurantNumber);
		assignment.setAddress(address);
		assignment.setLicenseHolder(licenseHolder);
		assignment.setHolderName(HOLDER_NAME);
		assignment.setPremisesName(PREMISES_NAME);
		assignment.setValidFrom(VALID_FROM);
		assignment.setValidTo(VALID_TO);
		assignment.setStatus(STATUS);
		assignment.setCreated(CREATED);

		assertThat(assignment.getId()).isEqualTo(ID);
		assertThat(assignment.getRestaurantNumber()).isEqualTo(restaurantNumber);
		assertThat(assignment.getAddress()).isEqualTo(address);
		assertThat(assignment.getLicenseHolder()).isEqualTo(licenseHolder);
		assertThat(assignment.getHolderName()).isEqualTo(HOLDER_NAME);
		assertThat(assignment.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(assignment.getValidFrom()).isEqualTo(VALID_FROM);
		assertThat(assignment.getValidTo()).isEqualTo(VALID_TO);
		assertThat(assignment.getStatus()).isEqualTo(STATUS);
		assertThat(assignment.getCreated()).isEqualTo(CREATED);
		assertThat(assignment).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new Assignment()).hasAllNullFieldsOrProperties();
	}
}
