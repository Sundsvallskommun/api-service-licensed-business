package se.sundsvall.licensedbusiness.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Addresses model")
public class Addresses {

	@JsonProperty("_meta")
	@Schema(implementation = PagingMetaData.class, accessMode = READ_ONLY)
	private PagingMetaData metaData;

	@ArraySchema(schema = @Schema(implementation = Address.class, accessMode = READ_ONLY))
	private List<Address> addresses;

	public static Addresses create() {
		return new Addresses();
	}

	public PagingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(PagingMetaData metaData) {
		this.metaData = metaData;
	}

	public Addresses withMetaData(PagingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public Addresses withAddresses(List<Address> addresses) {
		this.addresses = addresses;
		return this;
	}

	@Override
	public String toString() {
		return "Addresses{" +
			"metaData=" + metaData +
			", addresses=" + addresses +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Addresses that = (Addresses) o;
		return Objects.equals(metaData, that.metaData) && Objects.equals(addresses, that.addresses);
	}

	@Override
	public int hashCode() {
		return Objects.hash(metaData, addresses);
	}
}
