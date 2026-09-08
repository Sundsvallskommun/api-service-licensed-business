package se.sundsvall.licensedbusiness.integration.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;

import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

@Entity
@Table(name = "address", indexes = {
	@Index(name = "IDX_ADDRESS_MUNICIPALITY_ID", columnList = "municipality_id")
}, uniqueConstraints = {
	@UniqueConstraint(name = "UK_ADDRESS_STREET_POSTAL_MUNICIPALITY", columnNames = {
		"street_address", "postal_code", "municipality_id"
	})
})
public class AddressEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", columnDefinition = "VARCHAR(36)")
	private String id;

	@Column(name = "street_address", columnDefinition = "VARCHAR(255)")
	private String streetAddress;

	@Column(name = "postal_code", columnDefinition = "VARCHAR(10)")
	private String postalCode;

	@Column(name = "postal_area", columnDefinition = "VARCHAR(100)")
	private String postalArea;

	@Column(name = "municipality_id", columnDefinition = "VARCHAR(6)")
	private String municipalityId;

	@Column(name = "created", columnDefinition = "DATETIME")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@PrePersist
	void prePersist() {
		created = OffsetDateTime.now(ZoneId.systemDefault());
	}

	public static AddressEntity create() {
		return new AddressEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AddressEntity withId(String id) {
		this.id = id;
		return this;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public AddressEntity withStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
		return this;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public AddressEntity withPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public String getPostalArea() {
		return postalArea;
	}

	public void setPostalArea(String postalArea) {
		this.postalArea = postalArea;
	}

	public AddressEntity withPostalArea(String postalArea) {
		this.postalArea = postalArea;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public AddressEntity withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public AddressEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		AddressEntity that = (AddressEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(streetAddress, that.streetAddress) && Objects.equals(postalCode, that.postalCode) && Objects.equals(postalArea, that.postalArea) && Objects.equals(municipalityId, that.municipalityId)
			&& Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, streetAddress, postalCode, postalArea, municipalityId, created);
	}

	@Override
	public String toString() {
		return "AddressEntity{" +
			"id='" + id + '\'' +
			", streetAddress='" + streetAddress + '\'' +
			", postalCode='" + postalCode + '\'' +
			", postalArea='" + postalArea + '\'' +
			", municipalityId='" + municipalityId + '\'' +
			", created=" + created +
			'}';
	}
}
