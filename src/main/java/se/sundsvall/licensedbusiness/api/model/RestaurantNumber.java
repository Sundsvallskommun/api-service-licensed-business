package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "Restaurant number model")
public class RestaurantNumber {

	@Schema(description = "Restaurant number ID", examples = "9ce333ec-a473-438b-8406-a71e957dc107")
	private String id;

	@Schema(description = "Restaurant number", examples = "1001")
	private String number;

	@Schema(description = "Municipality ID", examples = "2281")
	private String municipalityId;

	@Schema(description = "Timestamp when the restaurant number was created")
	private OffsetDateTime created;

	public static RestaurantNumber create() {
		return new RestaurantNumber();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RestaurantNumber withId(String id) {
		this.id = id;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public RestaurantNumber withNumber(String number) {
		this.number = number;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public RestaurantNumber withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public RestaurantNumber withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		RestaurantNumber that = (RestaurantNumber) o;
		return Objects.equals(id, that.id) && Objects.equals(number, that.number) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, number, municipalityId, created);
	}

	@Override
	public String toString() {
		return "RestaurantNumber{" +
			"id='" + id + '\'' +
			", number='" + number + '\'' +
			", municipalityId='" + municipalityId + '\'' +
			", created=" + created +
			'}';
	}
}
