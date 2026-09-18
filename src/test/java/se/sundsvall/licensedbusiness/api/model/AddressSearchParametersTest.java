package se.sundsvall.licensedbusiness.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class AddressSearchParametersTest {

	private static final int PAGE = 1;
	private static final int LIMIT = 20;
	private static final List<String> SORT_BY = List.of("streetAddress");
	private static final Sort.Direction SORT_DIRECTION = Sort.Direction.DESC;
	private static final String QUERY = "Storgatan";

	@Test
	void testBean() {
		assertThat(AddressSearchParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void setterAndGetterTest() {
		final var searchParameters = new AddressSearchParameters();
		searchParameters.setPage(PAGE);
		searchParameters.setLimit(LIMIT);
		searchParameters.setSortBy(SORT_BY);
		searchParameters.setSortDirection(SORT_DIRECTION);
		searchParameters.setQuery(QUERY);

		assertThat(searchParameters.getPage()).isEqualTo(PAGE);
		assertThat(searchParameters.getLimit()).isEqualTo(LIMIT);
		assertThat(searchParameters.getSortBy()).isEqualTo(SORT_BY);
		assertThat(searchParameters.getSortDirection()).isEqualTo(SORT_DIRECTION);
		assertThat(searchParameters.getQuery()).isEqualTo(QUERY);
		assertThat(searchParameters).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		final var searchParameters = new AddressSearchParameters();

		assertThat(searchParameters).hasAllNullFieldsOrPropertiesExcept("page", "limit", "sortDirection");
		assertThat(searchParameters.getPage()).isEqualTo(1);
		assertThat(searchParameters.getLimit()).isEqualTo(100);
		assertThat(searchParameters.getSortDirection()).isEqualTo(Sort.DEFAULT_DIRECTION);
	}
}
