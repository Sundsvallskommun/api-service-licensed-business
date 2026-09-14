package se.sundsvall.licensedbusiness.integration.db.dao;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.licensedbusiness.integration.db.model.AddressEntity;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, String> {
	Optional<AddressEntity> findByStreetAddressAndPostalCodeAndMunicipalityId(String streetAddress, String postalCode, String municipalityId);

	Page<AddressEntity> findAllByMunicipalityId(String municipalityId, Pageable pageable);

	Page<AddressEntity> findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase(String municipalityId, String streetAddress, Pageable pageable);
}
