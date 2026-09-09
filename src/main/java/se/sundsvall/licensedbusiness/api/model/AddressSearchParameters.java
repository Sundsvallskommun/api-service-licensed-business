package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Address search and paging parameters")
public class AddressSearchParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Search query", examples = "Storgatan 1")
	private String query;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final var that = (AddressSearchParameters) o;
		return getPage() == that.getPage() && getLimit() == that.getLimit() && Objects.equals(getSortBy(), that.getSortBy()) && getSortDirection() == that.getSortDirection() && Objects.equals(query, that.query);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPage(), getLimit(), getSortBy(), getSortDirection(), query);
	}

	@Override
	public String toString() {
		return "AddressSearchParameters{" +
			"page=" + getPage() +
			", limit=" + getLimit() +
			", sortBy=" + getSortBy() +
			", sortDirection=" + getSortDirection() +
			", query='" + query + '\'' +
			'}';
	}
}
