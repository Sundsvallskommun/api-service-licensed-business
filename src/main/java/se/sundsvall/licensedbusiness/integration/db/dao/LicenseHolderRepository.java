package se.sundsvall.licensedbusiness.integration.db.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;

@Repository
public interface LicenseHolderRepository extends JpaRepository<LicenseHolderEntity, String> {
	Optional<LicenseHolderEntity> findByOrgNumber(String orgNumber);
}
