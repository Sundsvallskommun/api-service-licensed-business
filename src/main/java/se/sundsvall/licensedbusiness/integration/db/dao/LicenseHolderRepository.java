package se.sundsvall.licensedbusiness.integration.db.dao;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.licensedbusiness.integration.db.model.LicenseHolderEntity;

@CircuitBreaker(name = "licenseHolderRepository")
public interface LicenseHolderRepository extends JpaRepository<LicenseHolderEntity, String> {
	Optional<LicenseHolderEntity> findByOrgNumber(String orgNumber);
}
