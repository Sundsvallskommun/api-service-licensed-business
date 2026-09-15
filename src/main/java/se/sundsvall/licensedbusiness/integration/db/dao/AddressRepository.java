package se.sundsvall.licensedbusiness.integration.db.dao;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;

@CircuitBreaker(name = "addressRepository")
public interface AddressRepository extends JpaRepository<AddressEntity, String> {
	Optional<AddressEntity> findByStreetAddressAndPostalCodeAndMunicipalityId(String streetAddress, String postalCode, String municipalityId);

	boolean existsByIdAndMunicipalityId(String id, String municipalityId);

	Page<AddressEntity> findAllByMunicipalityId(String municipalityId, Pageable pageable);

	Page<AddressEntity> findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase(String municipalityId, String streetAddress, Pageable pageable);
}
