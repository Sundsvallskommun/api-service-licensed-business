package se.sundsvall.licensedbusiness.api.model;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class AssignmentCreateRequestTest {

	private static final String RESTAURANT_NUMBER_ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final String ADDRESS_ID = "8be2e4a1-3d4f-4b2e-9c18-2f6d5a7b1c33";
	private static final String ORG_NUMBER = "556612-4144";
	private static final String HOLDER_NAME = "Restaurang i Sundsvall AB";
	private static final String PREMISES_NAME = "Harrys Pub";
	private static final LocalDate VALID_FROM = LocalDate.of(2026, 1, 1);
	private static final LocalDate VALID_TO = LocalDate.of(2026, 12, 31);
	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> VALID_FROM.plusDays(SEQUENCE.incrementAndGet()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(AssignmentCreateRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var request = AssignmentCreateRequest.create()
			.withRestaurantNumberId(RESTAURANT_NUMBER_ID)
			.withAddressId(ADDRESS_ID)
			.withOrgNumber(ORG_NUMBER)
			.withHolderName(HOLDER_NAME)
			.withPremisesName(PREMISES_NAME)
			.withValidFrom(VALID_FROM)
			.withValidTo(VALID_TO);

		assertThat(request.getRestaurantNumberId()).isEqualTo(RESTAURANT_NUMBER_ID);
		assertThat(request.getAddressId()).isEqualTo(ADDRESS_ID);
		assertThat(request.getOrgNumber()).isEqualTo(ORG_NUMBER);
		assertThat(request.getHolderName()).isEqualTo(HOLDER_NAME);
		assertThat(request.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(request.getValidFrom()).isEqualTo(VALID_FROM);
		assertThat(request.getValidTo()).isEqualTo(VALID_TO);
		assertThat(request).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var request = new AssignmentCreateRequest();
		request.setRestaurantNumberId(RESTAURANT_NUMBER_ID);
		request.setAddressId(ADDRESS_ID);
		request.setOrgNumber(ORG_NUMBER);
		request.setHolderName(HOLDER_NAME);
		request.setPremisesName(PREMISES_NAME);
		request.setValidFrom(VALID_FROM);
		request.setValidTo(VALID_TO);

		assertThat(request.getRestaurantNumberId()).isEqualTo(RESTAURANT_NUMBER_ID);
		assertThat(request.getAddressId()).isEqualTo(ADDRESS_ID);
		assertThat(request.getOrgNumber()).isEqualTo(ORG_NUMBER);
		assertThat(request.getHolderName()).isEqualTo(HOLDER_NAME);
		assertThat(request.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(request.getValidFrom()).isEqualTo(VALID_FROM);
		assertThat(request.getValidTo()).isEqualTo(VALID_TO);
		assertThat(request).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new AssignmentCreateRequest()).hasAllNullFieldsOrProperties();
	}
}
