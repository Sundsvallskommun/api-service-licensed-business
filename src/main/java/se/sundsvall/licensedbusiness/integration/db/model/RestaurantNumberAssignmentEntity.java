package se.sundsvall.licensedbusiness.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

@Entity
@Table(name = "restaurant_number_assignment", indexes = {
	@Index(name = "IDX_ASSIGNMENT_RESTAURANT_NUMBER_ID", columnList = "restaurant_number_id"),
	@Index(name = "IDX_ASSIGNMENT_LICENSE_HOLDER_ID", columnList = "license_holder_id"),
	@Index(name = "IDX_ASSIGNMENT_ADDRESS_ID", columnList = "address_id"),
	@Index(name = "IDX_ASSIGNMENT_STATUS_VALID_TO", columnList = "status, valid_to")
})
public class RestaurantNumberAssignmentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, length = 36)
	private String id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_number_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ASSIGNMENT_RESTAURANT_NUMBER"))
	private RestaurantNumberEntity restaurantNumber;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "license_holder_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ASSIGNMENT_LICENSE_HOLDER"))
	private LicenseHolderEntity licenseHolder;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ASSIGNMENT_ADDRESS"))
	private AddressEntity address;

	// Why: holderName and premisesName are snapshots taken when the assignment was created. They must keep showing the
	// names that were registered for the period, even after the license holder is renamed.
	@Column(name = "holder_name", length = 255)
	private String holderName;

	@Column(name = "premises_name", length = 255)
	private String premisesName;

	@Column(name = "valid_from", nullable = false, columnDefinition = "DATE")
	private LocalDate validFrom;

	@Column(name = "valid_to", columnDefinition = "DATE")
	private LocalDate validTo;

	@Column(name = "status", columnDefinition = "VARCHAR(50)")
	@Enumerated(EnumType.STRING)
	private AssignmentStatus status;

	@Version
	@Column(name = "version", nullable = false)
	private long version;

	@Column(name = "created", columnDefinition = "DATETIME")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified", columnDefinition = "DATETIME")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime modified;

	@PrePersist
	void prePersist() {
		created = OffsetDateTime.now(ZoneId.systemDefault());
	}

	@PreUpdate
	void preUpdate() {
		modified = OffsetDateTime.now(ZoneId.systemDefault());
	}

	public static RestaurantNumberAssignmentEntity create() {
		return new RestaurantNumberAssignmentEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RestaurantNumberAssignmentEntity withId(String id) {
		this.id = id;
		return this;
	}

	public RestaurantNumberEntity getRestaurantNumber() {
		return restaurantNumber;
	}

	public void setRestaurantNumber(RestaurantNumberEntity restaurantNumber) {
		this.restaurantNumber = restaurantNumber;
	}

	public RestaurantNumberAssignmentEntity withRestaurantNumber(RestaurantNumberEntity restaurantNumber) {
		this.restaurantNumber = restaurantNumber;
		return this;
	}

	public LicenseHolderEntity getLicenseHolder() {
		return licenseHolder;
	}

	public void setLicenseHolder(LicenseHolderEntity licenseHolder) {
		this.licenseHolder = licenseHolder;
	}

	public RestaurantNumberAssignmentEntity withLicenseHolder(LicenseHolderEntity licenseHolder) {
		this.licenseHolder = licenseHolder;
		return this;
	}

	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}

	public RestaurantNumberAssignmentEntity withAddress(AddressEntity address) {
		this.address = address;
		return this;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public RestaurantNumberAssignmentEntity withHolderName(String holderName) {
		this.holderName = holderName;
		return this;
	}

	public String getPremisesName() {
		return premisesName;
	}

	public void setPremisesName(String premisesName) {
		this.premisesName = premisesName;
	}

	public RestaurantNumberAssignmentEntity withPremisesName(String premisesName) {
		this.premisesName = premisesName;
		return this;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public RestaurantNumberAssignmentEntity withValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	public LocalDate getValidTo() {
		return validTo;
	}

	public void setValidTo(LocalDate validTo) {
		this.validTo = validTo;
	}

	public RestaurantNumberAssignmentEntity withValidTo(LocalDate validTo) {
		this.validTo = validTo;
		return this;
	}

	public AssignmentStatus getStatus() {
		return status;
	}

	public void setStatus(AssignmentStatus status) {
		this.status = status;
	}

	public RestaurantNumberAssignmentEntity withStatus(AssignmentStatus status) {
		this.status = status;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public RestaurantNumberAssignmentEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(OffsetDateTime modified) {
		this.modified = modified;
	}

	public RestaurantNumberAssignmentEntity withModified(OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public RestaurantNumberAssignmentEntity withVersion(long version) {
		this.version = version;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		RestaurantNumberAssignmentEntity that = (RestaurantNumberAssignmentEntity) o;
		return version == that.version && Objects.equals(id, that.id) && Objects.equals(holderName, that.holderName) && Objects.equals(premisesName, that.premisesName) && Objects.equals(validFrom, that.validFrom)
			&& Objects.equals(validTo, that.validTo) && status == that.status && Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, holderName, premisesName, validFrom, validTo, status, version, created, modified);
	}

	@Override
	public String toString() {
		return "RestaurantNumberAssignmentEntity{" +
			"id='" + id + '\'' +
			", holderName='" + holderName + '\'' +
			", premisesName='" + premisesName + '\'' +
			", validFrom=" + validFrom +
			", validTo=" + validTo +
			", status=" + status +
			", version=" + version +
			", modified=" + modified +
			", created=" + created +
			'}';
	}
}
