package se.sundsvall.licensedbusiness.api.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressSearchParametersTest {

	@Test
	void gettersAndSetters() {
		final var searchParameters = new AddressSearchParameters();
		searchParameters.setQuery("Storgatan");

		assertThat(searchParameters.getQuery()).isEqualTo("Storgatan");
	}

	@Test
	void equalsAndHashCode() {
		final var first = new AddressSearchParameters();
		first.setPage(1);
		first.setLimit(20);
		first.setQuery("Storgatan");

		final var second = new AddressSearchParameters();
		second.setPage(1);
		second.setLimit(20);
		second.setQuery("Storgatan");

		final var differentQuery = new AddressSearchParameters();
		differentQuery.setPage(1);
		differentQuery.setLimit(20);
		differentQuery.setQuery("Kajplats");

		assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);
		assertThat(first).isNotEqualTo(differentQuery);
		assertThat(first).isNotEqualTo(null);
		assertThat(first).isNotEqualTo("not a search parameters instance");
		assertThat(first).isEqualTo(first);
	}

	@Test
	void toStringContainsFields() {
		final var searchParameters = new AddressSearchParameters();
		searchParameters.setPage(1);
		searchParameters.setLimit(20);
		searchParameters.setQuery("Storgatan");

		assertThat(searchParameters.toString()).contains("page=1", "limit=20", "query='Storgatan'");
	}
}
