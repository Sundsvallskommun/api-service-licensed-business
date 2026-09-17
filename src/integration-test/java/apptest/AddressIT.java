package apptest;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.licensedbusiness.Application;
import se.sundsvall.licensedbusiness.api.model.Address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@WireMockAppTestSuite(files = "classpath:/AddressIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class AddressIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String OTHER_MUNICIPALITY_ID = "2260";
	private static final String PATH = "/{municipalityId}/addresses";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_getAddresses() {
		setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getAddress() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{addressId}").build(MUNICIPALITY_ID, "it-address-1").toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_searchAddresses() {
		setupCall()
			.withServicePath(fromPath(PATH + "/search").queryParam("query", "provgatan").build(MUNICIPALITY_ID).toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_lookupAddress() {
		setupCall()
			.withServicePath(lookupPath(MUNICIPALITY_ID, "Provgatan 1", "852 30"))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_lookupAddressWithEveryNormalizedSpelling() {
		final var spellings = List.of(
			new String[] {
				"Provgatan 3B", "852 30"
			},
			new String[] {
				"provgatan 3b", "85230"
			},
			new String[] {
				"  PROVGATAN   3 B  ", "  852 30  "
			},
			new String[] {
				"Provgatan 3 b", "852 30"
			});

		spellings.forEach(spelling -> setupCall()
			.withServicePath(lookupPath(MUNICIPALITY_ID, spelling[0], spelling[1]))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse());
	}

	@Test
	void test06_lookupUnknownAddress() {
		setupCall()
			.withServicePath(lookupPath(MUNICIPALITY_ID, "Okändgatan 99", "852 99"))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_lookupAddressResolvesWithinTheMunicipality() {
		setupCall()
			.withServicePath(lookupPath(OTHER_MUNICIPALITY_ID, "Provgatan 1", "852 30"))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_lookupAddressInOtherMunicipality() {
		setupCall()
			.withServicePath(lookupPath(OTHER_MUNICIPALITY_ID, "Testvägen 12", "852 31"))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_createAddress() {
		final var location = setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(ALL_VALUE))
			.sendRequestAndVerifyResponse()
			.getResponseHeaders()
			.getLocation();

		assertThat(location).isNotNull();

		setupCall()
			.withServicePath(location.getPath())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_createAlreadyExistingAddress() {
		setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CONFLICT)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// DRAKEN-4918: a lookup miss must be followed by a create that succeeds, never by a 409.
	@Test
	void test11_lookupMissIsFollowedByASuccessfulCreate() throws Exception {
		setupCall()
			.withServicePath(lookupPath(MUNICIPALITY_ID, "nygatan 7 a", "85299"))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequest();

		final var location = setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.sendRequest()
			.getResponseHeaders()
			.getLocation();

		assertThat(location).isNotNull();

		final var lookedUp = setupCall()
			.withServicePath(lookupPath(MUNICIPALITY_ID, "Nygatan 7A", "852 99"))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.sendRequest()
			.andReturnBody(Address.class);

		assertThat(location.getPath()).endsWith("/" + lookedUp.getId());
		assertThat(lookedUp.getStreetAddress()).isEqualToIgnoringCase("Nygatan 7A");
		assertThat(lookedUp.getPostalCode()).isEqualTo("852 99");
		assertThat(lookedUp.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
	}

	// The rest template encodes the path it is given, so the query values are handed over unencoded.
	private static String lookupPath(final String municipalityId, final String streetAddress, final String postalCode) {
		return "/%s/addresses/lookup?streetAddress=%s&postalCode=%s".formatted(municipalityId, streetAddress, postalCode);
	}
}
