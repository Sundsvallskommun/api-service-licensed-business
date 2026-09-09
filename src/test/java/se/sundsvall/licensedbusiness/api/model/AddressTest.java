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

class AddressTest {

	private static final String ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String STREET_ADDRESS = "Storgatan 1";
	private static final String POSTAL_CODE = "852 30";
	private static final String POSTAL_AREA = "Sundsvall";
	private static final String MUNICIPALITY_ID = "2281";
	private static final OffsetDateTime CREATED = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, UTC);
	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> CREATED.plusDays(SEQUENCE.incrementAndGet()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(Address.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var address = Address.create()
			.withId(ID)
			.withStreetAddress(STREET_ADDRESS)
			.withPostalCode(POSTAL_CODE)
			.withPostalArea(POSTAL_AREA)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withCreated(CREATED);

		assertThat(address.getId()).isEqualTo(ID);
		assertThat(address.getStreetAddress()).isEqualTo(STREET_ADDRESS);
		assertThat(address.getPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(address.getPostalArea()).isEqualTo(POSTAL_AREA);
		assertThat(address.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(address.getCreated()).isEqualTo(CREATED);
		assertThat(address).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var address = new Address();
		address.setId(ID);
		address.setStreetAddress(STREET_ADDRESS);
		address.setPostalCode(POSTAL_CODE);
		address.setPostalArea(POSTAL_AREA);
		address.setMunicipalityId(MUNICIPALITY_ID);
		address.setCreated(CREATED);

		assertThat(address.getId()).isEqualTo(ID);
		assertThat(address.getStreetAddress()).isEqualTo(STREET_ADDRESS);
		assertThat(address.getPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(address.getPostalArea()).isEqualTo(POSTAL_AREA);
		assertThat(address.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(address.getCreated()).isEqualTo(CREATED);
		assertThat(address).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new Address()).hasAllNullFieldsOrProperties();
	}
}
