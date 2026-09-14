package se.sundsvall.licensedbusiness.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.models.api.paging.PagingMetaData;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class AddressesTest {

	private static final List<Address> ADDRESSES = List.of(new Address());
	private static final PagingMetaData META_DATA = PagingMetaData.create();

	@Test
	void testBean() {
		assertThat(Addresses.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderTest() {
		final var addresses = Addresses.create()
			.withMetaData(META_DATA)
			.withContent(ADDRESSES);

		assertThat(addresses.getMetaData()).isEqualTo(META_DATA);
		assertThat(addresses.getContent()).isEqualTo(ADDRESSES);
	}

	@Test
	void setterAndGetterTest() {
		final var addresses = new Addresses();
		addresses.setMetaData(META_DATA);
		addresses.setContent(ADDRESSES);

		assertThat(addresses.getMetaData()).isEqualTo(META_DATA);
		assertThat(addresses.getContent()).isEqualTo(ADDRESSES);
	}

	@Test
	void constructorTest() {
		assertThat(new Addresses()).hasAllNullFieldsOrProperties();
	}
}
