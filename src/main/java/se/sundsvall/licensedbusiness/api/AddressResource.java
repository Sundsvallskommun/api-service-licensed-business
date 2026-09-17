package se.sundsvall.licensedbusiness.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.api.model.AddressLookupParameters;
import se.sundsvall.licensedbusiness.api.model.AddressPagingParameters;
import se.sundsvall.licensedbusiness.api.model.AddressSearchParameters;
import se.sundsvall.licensedbusiness.api.model.Addresses;
import se.sundsvall.licensedbusiness.service.AddressService;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@Validated
@RestController
@Tag(name = "Address Resources")
@RequestMapping("/{municipalityId}/addresses")
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class AddressResource {

	private final AddressService addressService;

	AddressResource(final AddressService addressService) {
		this.addressService = addressService;
	}

	@Operation(summary = "Get a paged list of addresses", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	})
	@GetMapping
	ResponseEntity<Addresses> getAddresses(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid @ParameterObject final AddressPagingParameters pagingParameters) {
		return ResponseEntity.ok(addressService.getAddresses(municipalityId, pagingParameters));
	}

	@Operation(summary = "Get an address by ID", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@GetMapping("/{addressId}")
	ResponseEntity<Address> getAddress(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "addressId", description = "Address ID", example = "9ce333ec-a473-438b-8406-a71e957dc107") @PathVariable final String addressId) {
		return ResponseEntity.ok(addressService.getAddress(municipalityId, addressId));
	}

	@Operation(summary = "Search addresses", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	})
	@GetMapping("/search")
	ResponseEntity<Addresses> searchAddresses(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid @ParameterObject final AddressSearchParameters searchParameters) {
		return ResponseEntity.ok(addressService.searchAddresses(municipalityId, searchParameters));
	}

	@Operation(summary = "Look up a single address by street address and postal code",
		description = "Street address and postal code are matched on their normalized form, so case, extra whitespace, a space in the postal code and a house number letter written as \"1 A\" or \"1A\" all resolve to the same address. This is the same key the duplicate check on create uses.",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	@GetMapping("/lookup")
	ResponseEntity<Address> lookupAddress(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid @ParameterObject final AddressLookupParameters lookupParameters) {
		return ResponseEntity.ok(addressService.lookupAddress(municipalityId, lookupParameters));
	}

	@Operation(summary = "Create an address", responses = {
		@ApiResponse(responseCode = "201", description = "Created", headers = @Header(name = LOCATION, schema = @Schema(type = "string"))),
		@ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	@PostMapping
	ResponseEntity<Void> createAddress(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid @RequestBody final Address address) {
		final var addressId = addressService.createAddress(municipalityId, address);

		return ResponseEntity.created(fromPath("/{municipalityId}/addresses/{addressId}").buildAndExpand(municipalityId, addressId).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}
}
