package se.sundsvall.licensedbusiness.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@Validated
@RestController
@RequestMapping("/{municipalityId}/assignments")
class AssignmentResource {

	@PostMapping
	ResponseEntity<Void> createAssignment(@PathVariable @ValidMunicipalityId final String municipalityId) {
		throw Problem.valueOf(NOT_IMPLEMENTED, "Not yet implemented");
	}

	@PatchMapping("/{assignmentId}")
	ResponseEntity<Void> updateAssignment(@PathVariable @ValidMunicipalityId final String municipalityId, @PathVariable final String assignmentId) {
		throw Problem.valueOf(NOT_IMPLEMENTED, "Not yet implemented");
	}
}
