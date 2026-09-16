package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "License holder model")
public class LicenseHolder {

	@Schema(description = "License holder ID", examples = "9ce333ec-a473-438b-8406-a71e957dc107")
	private String id;

	@Schema(description = "Organization number", examples = "556612-4144")
	private String orgNumber;

	@Schema(description = "Name of the license holder", examples = "S.M.Å. Restaurang i Sundsvall AB")
	private String name;

	@Schema(description = "Timestamp when the license holder was created")
	private OffsetDateTime created;

	public static LicenseHolder create() {
		return new LicenseHolder();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LicenseHolder withId(String id) {
		this.id = id;
		return this;
	}

	public String getOrgNumber() {
		return orgNumber;
	}

	public void setOrgNumber(String orgNumber) {
		this.orgNumber = orgNumber;
	}

	public LicenseHolder withOrgNumber(String orgNumber) {
		this.orgNumber = orgNumber;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LicenseHolder withName(String name) {
		this.name = name;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public LicenseHolder withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		LicenseHolder that = (LicenseHolder) o;
		return Objects.equals(id, that.id) && Objects.equals(orgNumber, that.orgNumber) && Objects.equals(name, that.name) && Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, orgNumber, name, created);
	}

	@Override
	public String toString() {
		return "LicenseHolder{" +
			"id='" + id + '\'' +
			", orgNumber='" + orgNumber + '\'' +
			", name='" + name + '\'' +
			", created=" + created +
			'}';
	}
}
