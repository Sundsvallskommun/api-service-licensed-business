package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "Assignment model, describing a restaurant number assigned to a license holder at an address")
public class Assignment {

	@Schema(description = "Assignment ID", examples = "9ce333ec-a473-438b-8406-a71e957dc107")
	private String id;

	@Schema(description = "The assigned restaurant number")
	private RestaurantNumber restaurantNumber;

	@Schema(description = "The address the restaurant number is assigned to")
	private Address address;

	@Schema(description = "The license holder the restaurant number is assigned to")
	private LicenseHolder licenseHolder;

	@Schema(description = "Name of the license holder as registered for this assignment", examples = "S.M.Å. Restaurang i Sundsvall AB")
	private String holderName;

	@Schema(description = "Name of the premises", examples = "Harrys Pub & Restaurang")
	private String premisesName;

	@Schema(description = "First day the assignment is valid", examples = "2024-01-01")
	private LocalDate validFrom;

	@Schema(description = "Last day the assignment is valid, null if open ended", examples = "2024-12-31")
	private LocalDate validTo;

	@Schema(description = "Assignment status", examples = "ACTIVE")
	private String status;

	@Schema(description = "Timestamp when the assignment was created")
	private OffsetDateTime created;

	public static Assignment create() {
		return new Assignment();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Assignment withId(String id) {
		this.id = id;
		return this;
	}

	public RestaurantNumber getRestaurantNumber() {
		return restaurantNumber;
	}

	public void setRestaurantNumber(RestaurantNumber restaurantNumber) {
		this.restaurantNumber = restaurantNumber;
	}

	public Assignment withRestaurantNumber(RestaurantNumber restaurantNumber) {
		this.restaurantNumber = restaurantNumber;
		return this;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Assignment withAddress(Address address) {
		this.address = address;
		return this;
	}

	public LicenseHolder getLicenseHolder() {
		return licenseHolder;
	}

	public void setLicenseHolder(LicenseHolder licenseHolder) {
		this.licenseHolder = licenseHolder;
	}

	public Assignment withLicenseHolder(LicenseHolder licenseHolder) {
		this.licenseHolder = licenseHolder;
		return this;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public Assignment withHolderName(String holderName) {
		this.holderName = holderName;
		return this;
	}

	public String getPremisesName() {
		return premisesName;
	}

	public void setPremisesName(String premisesName) {
		this.premisesName = premisesName;
	}

	public Assignment withPremisesName(String premisesName) {
		this.premisesName = premisesName;
		return this;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public Assignment withValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	public LocalDate getValidTo() {
		return validTo;
	}

	public void setValidTo(LocalDate validTo) {
		this.validTo = validTo;
	}

	public Assignment withValidTo(LocalDate validTo) {
		this.validTo = validTo;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Assignment withStatus(String status) {
		this.status = status;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public Assignment withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Assignment that = (Assignment) o;
		return Objects.equals(id, that.id) && Objects.equals(restaurantNumber, that.restaurantNumber) && Objects.equals(address, that.address) && Objects.equals(licenseHolder, that.licenseHolder)
			&& Objects.equals(holderName, that.holderName) && Objects.equals(premisesName, that.premisesName) && Objects.equals(validFrom, that.validFrom) && Objects.equals(validTo, that.validTo)
			&& Objects.equals(status, that.status) && Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, restaurantNumber, address, licenseHolder, holderName, premisesName, validFrom, validTo, status, created);
	}

	@Override
	public String toString() {
		return "Assignment{" +
			"id='" + id + '\'' +
			", restaurantNumber=" + restaurantNumber +
			", address=" + address +
			", licenseHolder=" + licenseHolder +
			", holderName='" + holderName + '\'' +
			", premisesName='" + premisesName + '\'' +
			", validFrom=" + validFrom +
			", validTo=" + validTo +
			", status='" + status + '\'' +
			", created=" + created +
			'}';
	}
}
