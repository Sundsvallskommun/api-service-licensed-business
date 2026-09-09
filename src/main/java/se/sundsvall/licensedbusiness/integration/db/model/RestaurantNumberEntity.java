package se.sundsvall.licensedbusiness.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;

import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

@Entity
@Table(name = "restaurant_number", indexes = {
	@Index(name = "IDX_RESTAURANT_NUMBER_ADDRESS_ID", columnList = "address_id"),
	@Index(name = "IDX_RESTAURANT_NUMBER_MUNICIPALITY_ID", columnList = "municipality_id")
}, uniqueConstraints = {
	@UniqueConstraint(name = "UK_RESTAURANT_NUMBER", columnNames = {
		"restaurant_number"
	})
})
public class RestaurantNumberEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", columnDefinition = "VARCHAR(36)")
	private String id;

	@Column(name = "restaurant_number", columnDefinition = "VARCHAR(20)")
	private String restaurantNumber;

	@Column(name = "municipality_id", columnDefinition = "VARCHAR(6)")
	private String municipalityId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id", columnDefinition = "VARCHAR(36)", foreignKey = @ForeignKey(name = "FK_RESTAURANT_NUMBER_ADDRESS"))
	private AddressEntity address;

	@Column(name = "created", columnDefinition = "DATETIME")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@PrePersist
	void prePersist() {
		created = OffsetDateTime.now(ZoneId.systemDefault());
	}

	public static RestaurantNumberEntity create() {
		return new RestaurantNumberEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RestaurantNumberEntity withId(String id) {
		this.id = id;
		return this;
	}

	public String getRestaurantNumber() {
		return restaurantNumber;
	}

	public void setRestaurantNumber(String restaurantNumber) {
		this.restaurantNumber = restaurantNumber;
	}

	public RestaurantNumberEntity withRestaurantNumber(String restaurantNumber) {
		this.restaurantNumber = restaurantNumber;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public RestaurantNumberEntity withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}

	public RestaurantNumberEntity withAddress(AddressEntity address) {
		this.address = address;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public RestaurantNumberEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	// address is intentionally excluded from equals/hashCode/toString to avoid touching a lazy proxy.
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		RestaurantNumberEntity that = (RestaurantNumberEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(restaurantNumber, that.restaurantNumber) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, restaurantNumber, municipalityId, created);
	}

	@Override
	public String toString() {
		return "RestaurantNumberEntity{" +
			"id='" + id + '\'' +
			", restaurantNumber='" + restaurantNumber + '\'' +
			", municipalityId='" + municipalityId + '\'' +
			", created=" + created +
			'}';
	}
}
