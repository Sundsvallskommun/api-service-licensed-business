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

class AddressPagingParametersTest {

	private static final int PAGE = 2;
	private static final int LIMIT = 10;
	private static final List<String> SORT_BY = List.of("streetAddress");
	private static final Sort.Direction SORT_DIRECTION = Sort.Direction.DESC;

	@Test
	void testBean() {
		assertThat(AddressPagingParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void setterAndGetterTest() {
		final var pagingParameters = new AddressPagingParameters();
		pagingParameters.setPage(PAGE);
		pagingParameters.setLimit(LIMIT);
		pagingParameters.setSortBy(SORT_BY);
		pagingParameters.setSortDirection(SORT_DIRECTION);

		assertThat(pagingParameters.getPage()).isEqualTo(PAGE);
		assertThat(pagingParameters.getLimit()).isEqualTo(LIMIT);
		assertThat(pagingParameters.getSortBy()).isEqualTo(SORT_BY);
		assertThat(pagingParameters.getSortDirection()).isEqualTo(SORT_DIRECTION);
		assertThat(pagingParameters).hasNoNullFieldsOrProperties();
	}

	@Test
	void constructorTest() {
		final var pagingParameters = new AddressPagingParameters();

		assertThat(pagingParameters).hasAllNullFieldsOrPropertiesExcept("page", "limit", "sortDirection");
		assertThat(pagingParameters.getPage()).isEqualTo(1);
		assertThat(pagingParameters.getLimit()).isEqualTo(100);
		assertThat(pagingParameters.getSortDirection()).isEqualTo(Sort.DEFAULT_DIRECTION);
	}
}
