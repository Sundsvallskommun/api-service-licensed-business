package se.sundsvall.licensedbusiness.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.licensedbusiness.service.AddressNormalizer.normalizePostalCode;
import static se.sundsvall.licensedbusiness.service.AddressNormalizer.normalizeStreetAddress;

class AddressNormalizerTest {

	@ParameterizedTest
	@CsvSource(delimiter = '|', ignoreLeadingAndTrailingWhitespace = false, value = {
		"Storgatan 1|Storgatan 1",
		"  Storgatan 1  |Storgatan 1",
		"Storgatan   1|Storgatan 1",
		"Storgatan\t1|Storgatan 1",
		"Storgatan 1A|Storgatan 1A",
		"Storgatan 1 A|Storgatan 1A",
		"Storgatan 1 a|Storgatan 1a",
		"Storgatan 1   B|Storgatan 1B",
		"Storgatan 1 tr|Storgatan 1 tr",
		"Kajplats 1|Kajplats 1",
		"Storgatan 1|Storgatan 1",
		" Storgatan 1 |Storgatan 1"
	})
	void normalizeStreetAddressTest(final String raw, final String expected) {
		assertThat(normalizeStreetAddress(raw)).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', ignoreLeadingAndTrailingWhitespace = false, value = {
		"852 30|852 30",
		"85230|852 30",
		"8523 0|852 30",
		" 852 30 |852 30",
		"852 30|852 30",
		"SE-852 30|SE-85230",
		"1234|1234"
	})
	void normalizePostalCodeTest(final String raw, final String expected) {
		assertThat(normalizePostalCode(raw)).isEqualTo(expected);
	}
}
