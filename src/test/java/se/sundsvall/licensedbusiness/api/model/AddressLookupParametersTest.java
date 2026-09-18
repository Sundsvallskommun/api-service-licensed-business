package se.sundsvall.licensedbusiness.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class AddressLookupParametersTest {

	private static final String STREET_ADDRESS = "Storgatan 1";
	private static final String POSTAL_CODE = "852 30";

	@Test
	void testBean() {
		assertThat(AddressLookupParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void setterAndGetterTest() {
		final var lookupParameters = new AddressLookupParameters();
		lookupParameters.setStreetAddress(STREET_ADDRESS);
		lookupParameters.setPostalCode(POSTAL_CODE);

		assertThat(lookupParameters.getStreetAddress()).isEqualTo(STREET_ADDRESS);
		assertThat(lookupParameters.getPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(lookupParameters).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		assertThat(new AddressLookupParameters()).hasAllNullFieldsOrProperties();
	}
}
