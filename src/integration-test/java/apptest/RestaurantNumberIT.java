package apptest;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.licensedbusiness.Application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@WireMockAppTestSuite(files = "classpath:/RestaurantNumberIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class RestaurantNumberIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/{municipalityId}/restaurant-numbers";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_getRestaurantNumber() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{restaurantNumber}").build(MUNICIPALITY_ID, "22810001").toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getUnknownRestaurantNumber() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{restaurantNumber}").build(MUNICIPALITY_ID, "22819999").toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// 22810001, 22810003 and 22810004 are taken, so the lowest free number is 22810002.
	@Test
	void test03_createRestaurantNumberAllocatesTheLowestFreeNumber() {
		final var location = setupCall()
			.withServicePath(fromPath(PATH).queryParam("addressId", "it-address-1").build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(ALL_VALUE))
			.sendRequestAndVerifyResponse()
			.getResponseHeaders()
			.getLocation();

		assertThat(location).isNotNull();
		assertThat(location.getPath()).isEqualTo("/2281/restaurant-numbers/22810002");

		setupCall()
			.withServicePath(location.getPath())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_getAvailableRestaurantNumbers() {
		setupCall()
			.withServicePath(availablePath("it-address-3"))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_getAvailableRestaurantNumbersForAnOccupiedAddress() {
		setupCall()
			.withServicePath(availablePath("it-address-1"))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_getAvailableRestaurantNumbersForUnknownAddress() {
		setupCall()
			.withServicePath(availablePath("does-not-exist"))
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_getLatestAssignment() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{restaurantNumber}/assignment").build(MUNICIPALITY_ID, "22810001").toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_getLatestAssignmentForNeverAssignedNumber() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{restaurantNumber}/assignment").build(MUNICIPALITY_ID, "22810003").toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	private static String availablePath(final String addressId) {
		return fromPath(PATH + "/available").queryParam("addressId", addressId).build(MUNICIPALITY_ID).toString();
	}
}
