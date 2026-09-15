package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Request model for assigning a restaurant number to a license holder at an address")
public class AssignmentCreateRequest {

	@NotBlank
	@Schema(description = "ID of the restaurant number to assign", examples = "9ce333ec-a473-438b-8406-a71e957dc107")
	private String restaurantNumberId;

	@NotBlank
	@Schema(description = "ID of the address the restaurant number is assigned to", examples = "8be2e4a1-3d4f-4b2e-9c18-2f6d5a7b1c33")
	private String addressId;

	@NotBlank
	@Schema(description = "Organization number of the license holder", examples = "556612-4144")
	private String orgNumber;

	@NotBlank
	@Schema(description = "Name of the license holder as registered for this assignment", examples = "Restaurang i Sundsvall AB")
	private String holderName;

	@Schema(description = "Name of the premises", examples = "Harrys Pub")
	private String premisesName;

	@NotNull
	@Schema(description = "First day the assignment is valid", examples = "2026-01-01")
	private LocalDate validFrom;

	@Schema(description = "Last day the assignment is valid, omit for an open ended assignment", examples = "2026-12-31")
	private LocalDate validTo;

	public static AssignmentCreateRequest create() {
		return new AssignmentCreateRequest();
	}

	public String getRestaurantNumberId() {
		return restaurantNumberId;
	}

	public void setRestaurantNumberId(String restaurantNumberId) {
		this.restaurantNumberId = restaurantNumberId;
	}

	public AssignmentCreateRequest withRestaurantNumberId(String restaurantNumberId) {
		this.restaurantNumberId = restaurantNumberId;
		return this;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public AssignmentCreateRequest withAddressId(String addressId) {
		this.addressId = addressId;
		return this;
	}

	public String getOrgNumber() {
		return orgNumber;
	}

	public void setOrgNumber(String orgNumber) {
		this.orgNumber = orgNumber;
	}

	public AssignmentCreateRequest withOrgNumber(String orgNumber) {
		this.orgNumber = orgNumber;
		return this;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public AssignmentCreateRequest withHolderName(String holderName) {
		this.holderName = holderName;
		return this;
	}

	public String getPremisesName() {
		return premisesName;
	}

	public void setPremisesName(String premisesName) {
		this.premisesName = premisesName;
	}

	public AssignmentCreateRequest withPremisesName(String premisesName) {
		this.premisesName = premisesName;
		return this;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public AssignmentCreateRequest withValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	public LocalDate getValidTo() {
		return validTo;
	}

	public void setValidTo(LocalDate validTo) {
		this.validTo = validTo;
	}

	public AssignmentCreateRequest withValidTo(LocalDate validTo) {
		this.validTo = validTo;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		AssignmentCreateRequest that = (AssignmentCreateRequest) o;
		return Objects.equals(restaurantNumberId, that.restaurantNumberId) && Objects.equals(addressId, that.addressId) && Objects.equals(orgNumber, that.orgNumber) && Objects.equals(holderName, that.holderName)
			&& Objects.equals(premisesName, that.premisesName) && Objects.equals(validFrom, that.validFrom) && Objects.equals(validTo, that.validTo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(restaurantNumberId, addressId, orgNumber, holderName, premisesName, validFrom, validTo);
	}

	@Override
	public String toString() {
		return "AssignmentCreateRequest{" +
			"restaurantNumberId='" + restaurantNumberId + '\'' +
			", addressId='" + addressId + '\'' +
			", orgNumber='" + orgNumber + '\'' +
			", holderName='" + holderName + '\'' +
			", premisesName='" + premisesName + '\'' +
			", validFrom=" + validFrom +
			", validTo=" + validTo +
			'}';
	}
}
