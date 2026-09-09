package se.sundsvall.licensedbusiness.integration.db.model;

import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

class RestaurantNumberEntityTest {

	private static final String ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String RESTAURANT_NUMBER = "22813670";
	private static final String MUNICIPALITY_ID = "2281";
	private static final OffsetDateTime CREATED = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, UTC);
	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> CREATED.plusDays(SEQUENCE.incrementAndGet()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(RestaurantNumberEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("address"),
			hasValidBeanEqualsExcluding("address"),
			hasValidBeanToStringExcluding("address")));
	}

	@Test
	void builderTest() {
		final var address = new AddressEntity();
		final var restaurantNumberEntity = RestaurantNumberEntity.create()
			.withId(ID)
			.withRestaurantNumber(RESTAURANT_NUMBER)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withAddress(address)
			.withCreated(CREATED);

		assertThat(restaurantNumberEntity.getId()).isEqualTo(ID);
		assertThat(restaurantNumberEntity.getRestaurantNumber()).isEqualTo(RESTAURANT_NUMBER);
		assertThat(restaurantNumberEntity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(restaurantNumberEntity.getAddress()).isEqualTo(address);
		assertThat(restaurantNumberEntity.getCreated()).isEqualTo(CREATED);
		assertThat(restaurantNumberEntity).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var address = new AddressEntity();
		final var restaurantNumberEntity = new RestaurantNumberEntity();
		restaurantNumberEntity.setId(ID);
		restaurantNumberEntity.setRestaurantNumber(RESTAURANT_NUMBER);
		restaurantNumberEntity.setMunicipalityId(MUNICIPALITY_ID);
		restaurantNumberEntity.setAddress(address);
		restaurantNumberEntity.setCreated(CREATED);

		assertThat(restaurantNumberEntity.getId()).isEqualTo(ID);
		assertThat(restaurantNumberEntity.getRestaurantNumber()).isEqualTo(RESTAURANT_NUMBER);
		assertThat(restaurantNumberEntity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(restaurantNumberEntity.getAddress()).isEqualTo(address);
		assertThat(restaurantNumberEntity.getCreated()).isEqualTo(CREATED);
		assertThat(restaurantNumberEntity).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new RestaurantNumberEntity()).hasAllNullFieldsOrProperties();
	}
}
