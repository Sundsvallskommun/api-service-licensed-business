package se.sundsvall.licensedbusiness.integration.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;

import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

@Entity
@Table(name = "license_holder")
public class LicenseHolderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", columnDefinition = "VARCHAR(36)")
	private String id;

	@Column(name = "org_number", columnDefinition = "VARCHAR(13)")
	private String orgNumber;

	@Column(name = "name", columnDefinition = "VARCHAR(255)")
	private String name;

	@Column(name = "created", columnDefinition = "DATETIME")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@PrePersist
	void prePersist() {
		created = OffsetDateTime.now(ZoneId.systemDefault());
	}

	public static LicenseHolderEntity create() {
		return new LicenseHolderEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LicenseHolderEntity withId(String id) {
		this.id = id;
		return this;
	}

	public String getOrgNumber() {
		return orgNumber;
	}

	public void setOrgNumber(String orgNumber) {
		this.orgNumber = orgNumber;
	}

	public LicenseHolderEntity withOrgNumber(String orgNumber) {
		this.orgNumber = orgNumber;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LicenseHolderEntity withName(String name) {
		this.name = name;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public LicenseHolderEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		LicenseHolderEntity that = (LicenseHolderEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(orgNumber, that.orgNumber) && Objects.equals(name, that.name) && Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, orgNumber, name, created);
	}

	@Override
	public String toString() {
		return "LicenseHolderEntity{" +
			"id='" + id + '\'' +
			", orgNumber='" + orgNumber + '\'' +
			", name='" + name + '\'' +
			", created=" + created +
			'}';
	}
}
