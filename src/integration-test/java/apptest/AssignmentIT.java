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
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@WireMockAppTestSuite(files = "classpath:/AssignmentIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class AssignmentIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/{municipalityId}/assignments";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_getAssignment() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{assignmentId}").build(MUNICIPALITY_ID, "it-assignment-1").toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getUnknownAssignment() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{assignmentId}").build(MUNICIPALITY_ID, "does-not-exist").toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// The org number in the request is the one it-holder-1 already has, written without the dash.
	@Test
	void test03_createAssignmentReusingAnExistingLicenseHolder() {
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

	// A new assignment starting after the running one ends the running one the day before.
	@Test
	void test04_createAssignmentTakesOverTheRestaurantNumber() {
		setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.sendRequest();

		setupCall()
			.withServicePath(fromPath(PATH + "/{assignmentId}").build(MUNICIPALITY_ID, "it-assignment-1").toString())
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response-ended-assignment.json")
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath("/2281/restaurant-numbers/22810001/assignment")
			.withHttpMethod(GET)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_createAssignmentWithOverlappingPeriod() {
		setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_createAssignmentAtAnAddressTheNumberDoesNotBelongTo() {
		setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_createAssignmentForUnknownAddress() {
		setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_updateAssignment() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{assignmentId}").build(MUNICIPALITY_ID, "it-assignment-1").toString())
			.withHttpMethod(PATCH)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_updateUnknownAssignment() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{assignmentId}").build(MUNICIPALITY_ID, "does-not-exist").toString())
			.withHttpMethod(PATCH)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
