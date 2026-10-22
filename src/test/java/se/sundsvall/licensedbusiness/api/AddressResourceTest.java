package se.sundsvall.licensedbusiness.api;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.licensedbusiness.Application;
import se.sundsvall.licensedbusiness.api.model.Address;
import se.sundsvall.licensedbusiness.api.model.AddressLookupParameters;
import se.sundsvall.licensedbusiness.api.model.AddressPagingParameters;
import se.sundsvall.licensedbusiness.api.model.AddressRestaurantNumber;
import se.sundsvall.licensedbusiness.api.model.AddressSearchParameters;
import se.sundsvall.licensedbusiness.api.model.Addresses;
import se.sundsvall.licensedbusiness.service.AddressService;
import se.sundsvall.licensedbusiness.service.RestaurantNumberService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class AddressResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/{municipalityId}/addresses";
	private static final String ADDRESS_PATH = "/{municipalityId}/addresses/{addressId}";
	private static final String RESTAURANT_NUMBERS_PATH = "/{municipalityId}/addresses/{addressId}/restaurant-numbers";
	private static final String SEARCH_PATH = "/{municipalityId}/addresses/search";
	private static final String LOOKUP_PATH = "/{municipalityId}/addresses/lookup";
	private static final String ADDRESS_ID = "123e4567-e89b-12d3-a456-426614174000";
	private static final OffsetDateTime CREATED = OffsetDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneOffset.UTC);

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private AddressService addressServiceMock;

	@MockitoBean
	private RestaurantNumberService restaurantNumberServiceMock;

	@Test
	void getAddresses() {
		final var pagingParameters = new AddressPagingParameters();
		pagingParameters.setPage(2);
		pagingParameters.setLimit(10);
		final var expected = Addresses.create().withContent(List.of(address()));
		when(addressServiceMock.getAddresses(MUNICIPALITY_ID, pagingParameters)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).queryParam("page", 2).queryParam("limit", 10).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Addresses.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(addressServiceMock).getAddresses(MUNICIPALITY_ID, pagingParameters);
		verifyNoMoreInteractions(addressServiceMock);
		verifyNoInteractions(restaurantNumberServiceMock);
	}

	@Test
	void getAddress() {
		final var expected = address();
		when(addressServiceMock.getAddress(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(ADDRESS_PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "addressId", ADDRESS_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Address.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(addressServiceMock).getAddress(MUNICIPALITY_ID, ADDRESS_ID);
		verifyNoMoreInteractions(addressServiceMock);
		verifyNoInteractions(restaurantNumberServiceMock);
	}

	@Test
	void getAddressRestaurantNumbers() {
		final var expected = List.of(AddressRestaurantNumber.create().withId("number-1").withNumber("22810001").withStatus("ACTIVE").withPremisesName("Harrys Pub"));
		when(restaurantNumberServiceMock.getAddressRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(RESTAURANT_NUMBERS_PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "addressId", ADDRESS_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(AddressRestaurantNumber.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(restaurantNumberServiceMock).getAddressRestaurantNumbers(MUNICIPALITY_ID, ADDRESS_ID);
		verifyNoMoreInteractions(restaurantNumberServiceMock);
		verifyNoInteractions(addressServiceMock);
	}

	@Test
	void searchAddresses() {
		final var searchParameters = new AddressSearchParameters();
		searchParameters.setQuery("Storgatan");
		final var expected = Addresses.create().withContent(List.of(address()));
		when(addressServiceMock.searchAddresses(MUNICIPALITY_ID, searchParameters)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH).queryParam("query", "Storgatan").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Addresses.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(addressServiceMock).searchAddresses(MUNICIPALITY_ID, searchParameters);
		verifyNoMoreInteractions(addressServiceMock);
		verifyNoInteractions(restaurantNumberServiceMock);
	}

	@Test
	void lookupAddress() {
		final var lookupParameters = new AddressLookupParameters();
		lookupParameters.setStreetAddress("Storgatan 1");
		lookupParameters.setPostalCode("852 30");
		final var expected = address();
		when(addressServiceMock.lookupAddress(MUNICIPALITY_ID, lookupParameters)).thenReturn(expected);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(LOOKUP_PATH).queryParam("streetAddress", "Storgatan 1").queryParam("postalCode", "852 30").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Address.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		verify(addressServiceMock).lookupAddress(MUNICIPALITY_ID, lookupParameters);
		verifyNoMoreInteractions(addressServiceMock);
		verifyNoInteractions(restaurantNumberServiceMock);
	}

	@Test
	void createAddress() {
		final var address = Address.create().withStreetAddress("Storgatan 1").withPostalCode("852 30").withPostalArea("Sundsvall");
		when(addressServiceMock.createAddress(MUNICIPALITY_ID, address)).thenReturn(ADDRESS_ID);

		webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(address)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().location("/2281/addresses/" + ADDRESS_ID)
			.expectBody().isEmpty();

		verify(addressServiceMock).createAddress(MUNICIPALITY_ID, address);
		verifyNoMoreInteractions(addressServiceMock);
		verifyNoInteractions(restaurantNumberServiceMock);
	}

	private static Address address() {
		return Address.create().withId(ADDRESS_ID).withStreetAddress("Storgatan 1").withPostalCode("852 30").withPostalArea("Sundsvall").withMunicipalityId(MUNICIPALITY_ID).withCreated(CREATED);
	}
}
