package se.sundsvall.licensedbusiness.integration.db.model;

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

class AddressEntityTest {

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
		assertThat(AddressEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var addressEntity = AddressEntity.create()
			.withId(ID)
			.withStreetAddress(STREET_ADDRESS)
			.withPostalCode(POSTAL_CODE)
			.withPostalArea(POSTAL_AREA)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withCreated(CREATED);

		assertThat(addressEntity.getId()).isEqualTo(ID);
		assertThat(addressEntity.getStreetAddress()).isEqualTo(STREET_ADDRESS);
		assertThat(addressEntity.getPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(addressEntity.getPostalArea()).isEqualTo(POSTAL_AREA);
		assertThat(addressEntity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(addressEntity.getCreated()).isEqualTo(CREATED);
		assertThat(addressEntity).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var addressEntity = new AddressEntity();
		addressEntity.setId(ID);
		addressEntity.setStreetAddress(STREET_ADDRESS);
		addressEntity.setPostalCode(POSTAL_CODE);
		addressEntity.setPostalArea(POSTAL_AREA);
		addressEntity.setMunicipalityId(MUNICIPALITY_ID);
		addressEntity.setCreated(CREATED);

		assertThat(addressEntity.getId()).isEqualTo(ID);
		assertThat(addressEntity.getStreetAddress()).isEqualTo(STREET_ADDRESS);
		assertThat(addressEntity.getPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(addressEntity.getPostalArea()).isEqualTo(POSTAL_AREA);
		assertThat(addressEntity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(addressEntity.getCreated()).isEqualTo(CREATED);
		assertThat(addressEntity).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new AddressEntity()).hasAllNullFieldsOrProperties();
	}
}
