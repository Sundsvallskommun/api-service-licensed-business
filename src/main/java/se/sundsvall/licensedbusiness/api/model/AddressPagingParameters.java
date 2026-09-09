package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Address paging and sorting parameters")
public class AddressPagingParameters extends AbstractParameterPagingAndSortingBase {

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final var that = (AddressPagingParameters) o;
		return getPage() == that.getPage() && getLimit() == that.getLimit() && Objects.equals(getSortBy(), that.getSortBy()) && getSortDirection() == that.getSortDirection();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPage(), getLimit(), getSortBy(), getSortDirection());
	}

	@Override
	public String toString() {
		return "AddressPagingParameters{" +
			"page=" + getPage() +
			", limit=" + getLimit() +
			", sortBy=" + getSortBy() +
			", sortDirection=" + getSortDirection() +
			'}';
	}
}
