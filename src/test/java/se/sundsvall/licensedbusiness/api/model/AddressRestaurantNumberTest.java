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

class AddressRestaurantNumberTest {

	private static final String ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String NUMBER = "22810001";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String STATUS = "ACTIVE";
	private static final String PREMISES_NAME = "Harrys Pub";
	private static final LocalDate VALID_FROM = LocalDate.of(2026, 1, 1);
	private static final LocalDate VALID_TO = LocalDate.of(2026, 12, 31);
	private static final OffsetDateTime CREATED = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, UTC);
	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> CREATED.plusDays(SEQUENCE.incrementAndGet()), OffsetDateTime.class);
		registerValueGenerator(() -> VALID_FROM.plusDays(SEQUENCE.incrementAndGet()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(AddressRestaurantNumber.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var addressRestaurantNumber = AddressRestaurantNumber.create()
			.withId(ID)
			.withNumber(NUMBER)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withStatus(STATUS)
			.withPremisesName(PREMISES_NAME)
			.withValidFrom(VALID_FROM)
			.withValidTo(VALID_TO)
			.withCreated(CREATED);

		assertThat(addressRestaurantNumber.getId()).isEqualTo(ID);
		assertThat(addressRestaurantNumber.getNumber()).isEqualTo(NUMBER);
		assertThat(addressRestaurantNumber.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(addressRestaurantNumber.getStatus()).isEqualTo(STATUS);
		assertThat(addressRestaurantNumber.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(addressRestaurantNumber.getValidFrom()).isEqualTo(VALID_FROM);
		assertThat(addressRestaurantNumber.getValidTo()).isEqualTo(VALID_TO);
		assertThat(addressRestaurantNumber.getCreated()).isEqualTo(CREATED);
		assertThat(addressRestaurantNumber).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var addressRestaurantNumber = new AddressRestaurantNumber();
		addressRestaurantNumber.setId(ID);
		addressRestaurantNumber.setNumber(NUMBER);
		addressRestaurantNumber.setMunicipalityId(MUNICIPALITY_ID);
		addressRestaurantNumber.setStatus(STATUS);
		addressRestaurantNumber.setPremisesName(PREMISES_NAME);
		addressRestaurantNumber.setValidFrom(VALID_FROM);
		addressRestaurantNumber.setValidTo(VALID_TO);
		addressRestaurantNumber.setCreated(CREATED);

		assertThat(addressRestaurantNumber.getId()).isEqualTo(ID);
		assertThat(addressRestaurantNumber.getNumber()).isEqualTo(NUMBER);
		assertThat(addressRestaurantNumber.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(addressRestaurantNumber.getStatus()).isEqualTo(STATUS);
		assertThat(addressRestaurantNumber.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(addressRestaurantNumber.getValidFrom()).isEqualTo(VALID_FROM);
		assertThat(addressRestaurantNumber.getValidTo()).isEqualTo(VALID_TO);
		assertThat(addressRestaurantNumber.getCreated()).isEqualTo(CREATED);
		assertThat(addressRestaurantNumber).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new AddressRestaurantNumber()).hasAllNullFieldsOrProperties();
	}
}
