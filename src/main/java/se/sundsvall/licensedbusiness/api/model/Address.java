package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "Address model")
public class Address {

	@Schema(description = "Address ID", examples = "9ce333ec-a473-438b-8406-a71e957dc107")
	private String id;

	@Schema(description = "Street address", examples = "Storgatan 1")
	private String streetAddress;

	@Schema(description = "Postal code", examples = "852 30")
	private String postalCode;

	@Schema(description = "Postal area", examples = "Sundsvall")
	private String postalArea;

	@Schema(description = "Municipality ID", examples = "2281")
	private String municipalityId;

	@Schema(description = "Timestamp when the address was created")
	private OffsetDateTime created;

	public static Address create() {
		return new Address();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Address withId(String id) {
		this.id = id;
		return this;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public Address withStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
		return this;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Address withPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public String getPostalArea() {
		return postalArea;
	}

	public void setPostalArea(String postalArea) {
		this.postalArea = postalArea;
	}

	public Address withPostalArea(String postalArea) {
		this.postalArea = postalArea;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public Address withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public Address withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Address that = (Address) o;
		return Objects.equals(id, that.id) && Objects.equals(streetAddress, that.streetAddress) && Objects.equals(postalCode, that.postalCode) && Objects.equals(postalArea, that.postalArea) && Objects.equals(municipalityId, that.municipalityId)
			&& Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, streetAddress, postalCode, postalArea, municipalityId, created);
	}

	@Override
	public String toString() {
		return "Address{" +
			"id='" + id + '\'' +
			", streetAddress='" + streetAddress + '\'' +
			", postalCode='" + postalCode + '\'' +
			", postalArea='" + postalArea + '\'' +
			", municipalityId='" + municipalityId + '\'' +
			", created=" + created +
			'}';
	}
}
