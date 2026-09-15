package se.sundsvall.licensedbusiness.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.Assignment;
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.service.AssignmentService;
import se.sundsvall.licensedbusiness.service.RestaurantNumberService;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@Validated
@RestController
@Tag(name = "Restaurant Number Resources")
@RequestMapping("/{municipalityId}/restaurant-numbers")
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class RestaurantNumberResource {

	private final RestaurantNumberService restaurantNumberService;

	private final AssignmentService assignmentService;

	RestaurantNumberResource(final RestaurantNumberService restaurantNumberService, final AssignmentService assignmentService) {
		this.restaurantNumberService = restaurantNumberService;
		this.assignmentService = assignmentService;
	}

	@Operation(summary = "Get available restaurant numbers for an address", responses = {
		@ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantNumber.class)))),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@GetMapping("/available")
	ResponseEntity<List<RestaurantNumber>> getAvailableRestaurantNumbers(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "addressId", description = "Address ID", example = "9ce333ec-a473-438b-8406-a71e957dc107") @RequestParam @NotBlank final String addressId) {
		return ResponseEntity.ok(restaurantNumberService.getAvailableRestaurantNumbers(municipalityId, addressId));
	}

	@Operation(summary = "Get a restaurant number", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@GetMapping("/{restaurantNumber}")
	ResponseEntity<RestaurantNumber> getRestaurantNumber(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "restaurantNumber", description = "Restaurant number", example = "22813670") @PathVariable final String restaurantNumber) {
		return ResponseEntity.ok(restaurantNumberService.getRestaurantNumber(municipalityId, restaurantNumber));
	}

	@Operation(summary = "Get the most recent assignment for a restaurant number, including its address and license holder", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@GetMapping("/{restaurantNumber}/assignment")
	ResponseEntity<Assignment> getLatestAssignment(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "restaurantNumber", description = "Restaurant number", example = "22813670") @PathVariable final String restaurantNumber) {
		return ResponseEntity.ok(assignmentService.getLatestAssignment(municipalityId, restaurantNumber));
	}

	@Operation(summary = "Create a restaurant number, allocating the lowest free number in the municipality", responses = {
		@ApiResponse(responseCode = "201", description = "Created", headers = @Header(name = LOCATION, schema = @Schema(type = "string"))),
		@ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@PostMapping
	ResponseEntity<Void> createRestaurantNumber(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId) {
		final var restaurantNumber = restaurantNumberService.createRestaurantNumber(municipalityId);

		return ResponseEntity.created(fromPath("/{municipalityId}/restaurant-numbers/{restaurantNumber}").buildAndExpand(municipalityId, restaurantNumber).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}
}
