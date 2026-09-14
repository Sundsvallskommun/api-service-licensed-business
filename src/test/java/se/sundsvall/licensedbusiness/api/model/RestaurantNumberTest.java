package se.sundsvall.licensedbusiness.api.model;

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

class RestaurantNumberTest {

	private static final String ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String NUMBER = "1001";
	private static final String MUNICIPALITY_ID = "2281";
	private static final OffsetDateTime CREATED = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, UTC);
	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> CREATED.plusDays(SEQUENCE.incrementAndGet()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(RestaurantNumber.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var restaurantNumber = RestaurantNumber.create()
			.withId(ID)
			.withNumber(NUMBER)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withCreated(CREATED);

		assertThat(restaurantNumber.getId()).isEqualTo(ID);
		assertThat(restaurantNumber.getNumber()).isEqualTo(NUMBER);
		assertThat(restaurantNumber.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(restaurantNumber.getCreated()).isEqualTo(CREATED);
		assertThat(restaurantNumber).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var restaurantNumber = new RestaurantNumber();
		restaurantNumber.setId(ID);
		restaurantNumber.setNumber(NUMBER);
		restaurantNumber.setMunicipalityId(MUNICIPALITY_ID);
		restaurantNumber.setCreated(CREATED);

		assertThat(restaurantNumber.getId()).isEqualTo(ID);
		assertThat(restaurantNumber.getNumber()).isEqualTo(NUMBER);
		assertThat(restaurantNumber.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(restaurantNumber.getCreated()).isEqualTo(CREATED);
		assertThat(restaurantNumber).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new RestaurantNumber()).hasAllNullFieldsOrProperties();
	}
}
