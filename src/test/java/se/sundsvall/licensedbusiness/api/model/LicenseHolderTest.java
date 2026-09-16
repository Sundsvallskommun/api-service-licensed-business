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

class LicenseHolderTest {

	private static final String ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String ORG_NUMBER = "556612-4144";
	private static final String NAME = "Restaurang i Sundsvall AB";
	private static final OffsetDateTime CREATED = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, UTC);
	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> CREATED.plusDays(SEQUENCE.incrementAndGet()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(LicenseHolder.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var licenseHolder = LicenseHolder.create()
			.withId(ID)
			.withOrgNumber(ORG_NUMBER)
			.withName(NAME)
			.withCreated(CREATED);

		assertThat(licenseHolder.getId()).isEqualTo(ID);
		assertThat(licenseHolder.getOrgNumber()).isEqualTo(ORG_NUMBER);
		assertThat(licenseHolder.getName()).isEqualTo(NAME);
		assertThat(licenseHolder.getCreated()).isEqualTo(CREATED);
		assertThat(licenseHolder).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var licenseHolder = new LicenseHolder();
		licenseHolder.setId(ID);
		licenseHolder.setOrgNumber(ORG_NUMBER);
		licenseHolder.setName(NAME);
		licenseHolder.setCreated(CREATED);

		assertThat(licenseHolder.getId()).isEqualTo(ID);
		assertThat(licenseHolder.getOrgNumber()).isEqualTo(ORG_NUMBER);
		assertThat(licenseHolder.getName()).isEqualTo(NAME);
		assertThat(licenseHolder.getCreated()).isEqualTo(CREATED);
		assertThat(licenseHolder).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new LicenseHolder()).hasAllNullFieldsOrProperties();
	}
}
