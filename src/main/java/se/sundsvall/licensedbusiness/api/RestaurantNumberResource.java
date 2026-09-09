package se.sundsvall.licensedbusiness.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import se.sundsvall.licensedbusiness.api.model.RestaurantNumber;
import se.sundsvall.licensedbusiness.service.RestaurantNumberService;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@Validated
@RestController
@Tag(name = "Restaurant Number Resources")
@RequestMapping("/{municipalityId}/restaurant-numbers")
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class RestaurantNumberResource {

	private final RestaurantNumberService restaurantNumberService;

	RestaurantNumberResource(final RestaurantNumberService restaurantNumberService) {
		this.restaurantNumberService = restaurantNumberService;
	}

	@Operation(summary = "Get available restaurant numbers for an address", responses = {
		@ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantNumber.class))))
	})
	@GetMapping("/available")
	ResponseEntity<List<RestaurantNumber>> getAvailableRestaurantNumbers(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "addressId", description = "Address ID", example = "9ce333ec-a473-438b-8406-a71e957dc107") @RequestParam final String addressId) {
		return ResponseEntity.ok(restaurantNumberService.getAvailableRestaurantNumbers(municipalityId, addressId));
	}

	@Operation(summary = "Create a restaurant number", responses = {
		@ApiResponse(responseCode = "201", description = "Created")
	})
	@PostMapping
	ResponseEntity<Void> createRestaurantNumber(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId) {
		throw Problem.valueOf(NOT_IMPLEMENTED, "Not yet implemented");
	}
}
