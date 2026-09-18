package apptest;

import java.io.FileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.licensedbusiness.Application;
import se.sundsvall.licensedbusiness.integration.db.dao.AddressRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

@WireMockAppTestSuite(files = "classpath:/ImportIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class ImportIT extends AbstractAppTest {

	private static final String PATH = "/2281/import/restaurant-numbers";
	private static final String RESPONSE_FILE = "response.json";

	@Autowired
	private AddressRepository addressRepository;

	// The first row brings a new address, holder and number. The second reuses Provgatan 1 and the
	// holder behind 556600-1122, so only the number and the assignment are new.
	@Test
	void test01_importRestaurantNumbers() throws FileNotFoundException {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequestFile("file", "import.csv")
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// Two rows spelling the same house number "60B" and "60 B" are one address, which is what the
	// register itself says by keeping the same restaurant number across the spelling change.
	@Test
	void test03_importMergesHouseNumberLetterSpellings() throws FileNotFoundException {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequestFile("file", "import.csv")
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		assertThat(addressRepository.findAllByMunicipalityId("2281", PageRequest.of(0, 100)).getContent())
			.filteredOn(address -> address.getStreetAddress().startsWith("Bokstavsgatan"))
			.singleElement()
			.satisfies(address -> assertThat(address.getStreetAddress()).isEqualTo("Bokstavsgatan 60B"));
	}

	@Test
	void test02_importRestaurantNumbersWithABrokenRow() throws FileNotFoundException {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withHeader(ACCEPT, APPLICATION_JSON_VALUE)
			.withRequestFile("file", "import.csv")
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
