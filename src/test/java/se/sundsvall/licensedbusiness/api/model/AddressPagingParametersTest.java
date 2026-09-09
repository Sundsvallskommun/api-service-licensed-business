package se.sundsvall.licensedbusiness.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class AddressPagingParametersTest {

	@Test
	void equalsAndHashCode() {
		final var first = new AddressPagingParameters();
		first.setPage(2);
		first.setLimit(10);
		first.setSortBy(List.of("streetAddress"));
		first.setSortDirection(Sort.Direction.DESC);

		final var second = new AddressPagingParameters();
		second.setPage(2);
		second.setLimit(10);
		second.setSortBy(List.of("streetAddress"));
		second.setSortDirection(Sort.Direction.DESC);

		final var different = new AddressPagingParameters();
		different.setPage(3);
		different.setLimit(10);

		assertThat(first)
			.isEqualTo(second)
			.hasSameHashCodeAs(second)
			.isNotEqualTo(different)
			.isNotEqualTo(null)
			.isNotEqualTo("not a paging parameters instance")
			.isEqualTo(first);
	}

	@Test
	void toStringContainsFields() {
		final var pagingParameters = new AddressPagingParameters();
		pagingParameters.setPage(1);
		pagingParameters.setLimit(20);

		assertThat(pagingParameters.toString()).contains("page=1", "limit=20");
	}
}
