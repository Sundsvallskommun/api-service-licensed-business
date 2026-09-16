package se.sundsvall.licensedbusiness.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.licensedbusiness.service.TextSanitizer.sanitize;

class TextSanitizerTest {

	private static final int TAB = 0x09;
	private static final int BELL = 0x07;
	private static final int SOFT_HYPHEN = 0x00AD;
	private static final int LINE_SEPARATOR = 0x2028;

	private static String around(final int codePoint) {
		return "a" + Character.toString(codePoint) + "b";
	}

	@Test
	void keepsSwedishCharacters() {
		assertThat(sanitize("Köpmangatan 1, Sundsvall")).isEqualTo("Köpmangatan 1, Sundsvall");
		assertThat(sanitize("Södra Allén 2")).isEqualTo("Södra Allén 2");
	}

	@Test
	void removesLineBreaks() {
		assertThat(sanitize("2281\r\nFAKE LOG LINE")).isEqualTo("2281 FAKE LOG LINE");
		assertThat(sanitize("a\nb")).isEqualTo("a b");
	}

	@Test
	void removesOtherControlAndFormatCharacters() {
		assertThat(sanitize(around(TAB))).isEqualTo("a b");
		assertThat(sanitize(around(BELL))).isEqualTo("a b");
		assertThat(sanitize(around(SOFT_HYPHEN))).isEqualTo("a b");
		assertThat(sanitize(around(LINE_SEPARATOR))).isEqualTo("a b");
	}

	@Test
	void collapsesTheSpacesItIntroducesAndTrims() {
		assertThat(sanitize("\r\nStorgatan 1\r\n")).isEqualTo("Storgatan 1");
	}

	@Test
	void leavesOrdinaryTextAlone() {
		assertThat(sanitize("Storgatan 1")).isEqualTo("Storgatan 1");
		assertThat(sanitize("")).isEmpty();
	}

	@Test
	void returnsNullForNull() {
		assertThat(sanitize(null)).isNull();
	}
}
