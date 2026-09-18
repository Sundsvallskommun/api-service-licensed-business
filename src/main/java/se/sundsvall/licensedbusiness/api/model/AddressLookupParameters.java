package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Schema(description = "Address lookup parameters")
public class AddressLookupParameters {

	@NotBlank
	@Schema(description = "Street address, written as in the address register", examples = "Storgatan 1")
	private String streetAddress;

	@NotBlank
	@Schema(description = "Postal code, with or without space", examples = "852 30")
	private String postalCode;

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final var that = (AddressLookupParameters) o;
		return Objects.equals(streetAddress, that.streetAddress) && Objects.equals(postalCode, that.postalCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(streetAddress, postalCode);
	}

	@Override
	public String toString() {
		return "AddressLookupParameters{" +
			"streetAddress='" + streetAddress + '\'' +
			", postalCode='" + postalCode + '\'' +
			'}';
	}
}
