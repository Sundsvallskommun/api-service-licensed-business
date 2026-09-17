package se.sundsvall.licensedbusiness.api.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressLookupParametersTest {

	@Test
	void gettersAndSetters() {
		final var lookupParameters = new AddressLookupParameters();
		lookupParameters.setStreetAddress("Storgatan 1");
		lookupParameters.setPostalCode("852 30");

		assertThat(lookupParameters.getStreetAddress()).isEqualTo("Storgatan 1");
		assertThat(lookupParameters.getPostalCode()).isEqualTo("852 30");
	}

	@Test
	void noDirtOnCreatedBean() {
		final var lookupParameters = new AddressLookupParameters();

		assertThat(lookupParameters.getStreetAddress()).isNull();
		assertThat(lookupParameters.getPostalCode()).isNull();
	}

	@Test
	void equalsAndHashCode() {
		final var first = new AddressLookupParameters();
		first.setStreetAddress("Storgatan 1");
		first.setPostalCode("852 30");

		final var second = new AddressLookupParameters();
		second.setStreetAddress("Storgatan 1");
		second.setPostalCode("852 30");

		final var differentPostalCode = new AddressLookupParameters();
		differentPostalCode.setStreetAddress("Storgatan 1");
		differentPostalCode.setPostalCode("852 31");

		assertThat(first)
			.isEqualTo(second)
			.hasSameHashCodeAs(second)
			.isNotEqualTo(differentPostalCode)
			.isNotEqualTo(null)
			.isNotEqualTo("not a lookup parameters instance")
			.isEqualTo(first);
	}

	@Test
	void toStringContainsFields() {
		final var lookupParameters = new AddressLookupParameters();
		lookupParameters.setStreetAddress("Storgatan 1");
		lookupParameters.setPostalCode("852 30");

		assertThat(lookupParameters.toString()).contains("streetAddress='Storgatan 1'", "postalCode='852 30'");
	}
}
