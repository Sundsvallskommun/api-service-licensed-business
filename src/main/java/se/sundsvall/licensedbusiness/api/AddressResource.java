package se.sundsvall.licensedbusiness.api;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@Validated
@RestController
@RequestMapping("/{municipalityId}/addresses")
class AddressResource {

	@GetMapping
	ResponseEntity<Void> getAddresses(@PathVariable @ValidMunicipalityId final String municipalityId, final Pageable pageable) {
		throw Problem.valueOf(NOT_IMPLEMENTED, "Not yet implemented");
	}

	@GetMapping("/{addressId}")
	ResponseEntity<Void> getAddress(@PathVariable @ValidMunicipalityId final String municipalityId, @PathVariable final String addressId) {
		throw Problem.valueOf(NOT_IMPLEMENTED, "Not yet implemented");
	}

	@GetMapping("/search")
	ResponseEntity<Void> searchAddresses(@PathVariable @ValidMunicipalityId final String municipalityId, @RequestParam final String query, final Pageable pageable) {
		throw Problem.valueOf(NOT_IMPLEMENTED, "Not yet implemented");
	}
}
