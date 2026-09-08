package se.sundsvall.licensedbusiness.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.licensedbusiness.api.model.ImportResult;
import se.sundsvall.licensedbusiness.service.ImportService;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

// Temporary resource for the one-off data migration from the legacy excel export. Remove once the
// historical restaurant number register has been imported.
@Validated
@RestController
@RequestMapping("/{municipalityId}/import")
class ImportResource {

	private final ImportService importService;

	ImportResource(final ImportService importService) {
		this.importService = importService;
	}

	@PostMapping(value = "/restaurant-numbers", consumes = MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<ImportResult> importRestaurantNumbers(@PathVariable @ValidMunicipalityId final String municipalityId, @RequestPart final MultipartFile file) {
		return ResponseEntity.ok(importService.importRestaurantNumbers(municipalityId, file));
	}
}
