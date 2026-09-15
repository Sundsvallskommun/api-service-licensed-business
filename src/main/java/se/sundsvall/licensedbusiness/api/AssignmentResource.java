package se.sundsvall.licensedbusiness.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.Assignment;
import se.sundsvall.licensedbusiness.api.model.AssignmentCreateRequest;
import se.sundsvall.licensedbusiness.api.model.AssignmentUpdateRequest;
import se.sundsvall.licensedbusiness.service.AssignmentService;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@Validated
@RestController
@Tag(name = "Assignment Resources")
@RequestMapping("/{municipalityId}/assignments")
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class AssignmentResource {

	private final AssignmentService assignmentService;

	AssignmentResource(final AssignmentService assignmentService) {
		this.assignmentService = assignmentService;
	}

	@Operation(summary = "Get an assignment by ID", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@GetMapping("/{assignmentId}")
	ResponseEntity<Assignment> getAssignment(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "assignmentId", description = "Assignment ID", example = "9ce333ec-a473-438b-8406-a71e957dc107") @PathVariable final String assignmentId) {
		return ResponseEntity.ok(assignmentService.getAssignment(municipalityId, assignmentId));
	}

	@Operation(summary = "Create an assignment, ending any active assignment on the same restaurant number", responses = {
		@ApiResponse(responseCode = "201", description = "Created", headers = @Header(name = LOCATION, schema = @Schema(type = "string"))),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@PostMapping
	ResponseEntity<Void> createAssignment(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid @RequestBody final AssignmentCreateRequest request) {
		final var assignmentId = assignmentService.createAssignment(municipalityId, request);

		return ResponseEntity.created(fromPath("/{municipalityId}/assignments/{assignmentId}").buildAndExpand(municipalityId, assignmentId).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@Operation(summary = "Update an assignment", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@PatchMapping("/{assignmentId}")
	ResponseEntity<Assignment> updateAssignment(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "assignmentId", description = "Assignment ID", example = "9ce333ec-a473-438b-8406-a71e957dc107") @PathVariable final String assignmentId,
		@Valid @RequestBody final AssignmentUpdateRequest request) {
		return ResponseEntity.ok(assignmentService.updateAssignment(municipalityId, assignmentId, request));
	}
}
