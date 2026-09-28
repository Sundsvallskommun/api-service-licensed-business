package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "Restaurant number at an address, with its status and the premises name and period of its current assignment. "
	+ "A number without a current assignment shows its previous one, and a number whose only assignments start later shows the next one.")
public class AddressRestaurantNumber {

	@Schema(description = "Restaurant number ID", examples = "9ce333ec-a473-438b-8406-a71e957dc107")
	private String id;

	@Schema(description = "Restaurant number", examples = "22810001")
	private String number;

	@Schema(description = "Municipality ID", examples = "2281")
	private String municipalityId;

	@Schema(description = "ACTIVE when the restaurant number has an active assignment, otherwise AVAILABLE", allowableValues = {
		"ACTIVE", "AVAILABLE"
	}, examples = "ACTIVE")
	private String status;

	@Schema(description = "Name of the premises on the shown assignment, empty when the number has never been assigned", examples = "Harrys Pub")
	private String premisesName;

	@Schema(description = "First day of the shown assignment", examples = "2026-01-01")
	private LocalDate validFrom;

	@Schema(description = "Last day of the shown assignment, empty when it is open ended", examples = "2026-12-31")
	private LocalDate validTo;

	@Schema(description = "Timestamp when the restaurant number was created")
	private OffsetDateTime created;

	public static AddressRestaurantNumber create() {
		return new AddressRestaurantNumber();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AddressRestaurantNumber withId(String id) {
		this.id = id;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public AddressRestaurantNumber withNumber(String number) {
		this.number = number;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public AddressRestaurantNumber withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AddressRestaurantNumber withStatus(String status) {
		this.status = status;
		return this;
	}

	public String getPremisesName() {
		return premisesName;
	}

	public void setPremisesName(String premisesName) {
		this.premisesName = premisesName;
	}

	public AddressRestaurantNumber withPremisesName(String premisesName) {
		this.premisesName = premisesName;
		return this;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public AddressRestaurantNumber withValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	public LocalDate getValidTo() {
		return validTo;
	}

	public void setValidTo(LocalDate validTo) {
		this.validTo = validTo;
	}

	public AddressRestaurantNumber withValidTo(LocalDate validTo) {
		this.validTo = validTo;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public AddressRestaurantNumber withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		AddressRestaurantNumber that = (AddressRestaurantNumber) o;
		return Objects.equals(id, that.id) && Objects.equals(number, that.number) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(status, that.status)
			&& Objects.equals(premisesName, that.premisesName) && Objects.equals(validFrom, that.validFrom) && Objects.equals(validTo, that.validTo) && Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, number, municipalityId, status, premisesName, validFrom, validTo, created);
	}

	@Override
	public String toString() {
		return "AddressRestaurantNumber{" +
			"id='" + id + '\'' +
			", number='" + number + '\'' +
			", municipalityId='" + municipalityId + '\'' +
			", status='" + status + '\'' +
			", premisesName='" + premisesName + '\'' +
			", validFrom=" + validFrom +
			", validTo=" + validTo +
			", created=" + created +
			'}';
	}
}
