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

class AssignmentUpdateRequestTest {

	private static final LocalDate VALID_TO = LocalDate.of(2026, 12, 31);
	private static final String PREMISES_NAME = "Harrys Pub";
	private static final String HOLDER_NAME = "Restaurang i Sundsvall AB";
	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> VALID_TO.plusDays(SEQUENCE.incrementAndGet()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(AssignmentUpdateRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var request = AssignmentUpdateRequest.create()
			.withValidTo(VALID_TO)
			.withPremisesName(PREMISES_NAME)
			.withHolderName(HOLDER_NAME);

		assertThat(request.getValidTo()).isEqualTo(VALID_TO);
		assertThat(request.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(request.getHolderName()).isEqualTo(HOLDER_NAME);
		assertThat(request).hasNoNullFieldsOrProperties();
	}

	@Test
	void setterAndGetterTest() {
		final var request = new AssignmentUpdateRequest();
		request.setValidTo(VALID_TO);
		request.setPremisesName(PREMISES_NAME);
		request.setHolderName(HOLDER_NAME);

		assertThat(request.getValidTo()).isEqualTo(VALID_TO);
		assertThat(request.getPremisesName()).isEqualTo(PREMISES_NAME);
		assertThat(request.getHolderName()).isEqualTo(HOLDER_NAME);
		assertThat(request).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new AssignmentUpdateRequest()).hasAllNullFieldsOrProperties();
	}
}
