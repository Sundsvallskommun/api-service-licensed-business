package se.sundsvall.licensedbusiness.api;

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

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@Validated
@RestController
@RequestMapping("/{municipalityId}/restaurant-numbers")
class RestaurantNumberResource {

	@GetMapping("/inactive")
	ResponseEntity<Void> getInactiveRestaurantNumbers(@PathVariable @ValidMunicipalityId final String municipalityId, @RequestParam final String addressId) {
		throw Problem.valueOf(NOT_IMPLEMENTED, "Not yet implemented");
	}

	@PostMapping
	ResponseEntity<Void> createRestaurantNumber(@PathVariable @ValidMunicipalityId final String municipalityId) {
		throw Problem.valueOf(NOT_IMPLEMENTED, "Not yet implemented");
	}
}
