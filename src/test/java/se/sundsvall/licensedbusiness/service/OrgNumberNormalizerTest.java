package se.sundsvall.licensedbusiness.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.licensedbusiness.service.OrgNumberNormalizer.normalize;

class OrgNumberNormalizerTest {

	@Test
	void addsTheDashToAnUndashedNumber() {
		assertThat(normalize("5566124144")).isEqualTo("556612-4144");
	}

	@Test
	void keepsAnAlreadyDashedNumber() {
		assertThat(normalize("556612-4144")).isEqualTo("556612-4144");
	}

	@Test
	void ignoresSpacesAndOtherSeparators() {
		assertThat(normalize(" 556612 4144 ")).isEqualTo("556612-4144");
		assertThat(normalize("556612 - 4144")).isEqualTo("556612-4144");
	}

	@Test
	void dropsTheCenturyPrefix() {
		assertThat(normalize("165566124144")).isEqualTo("556612-4144");
		assertThat(normalize("16556612-4144")).isEqualTo("556612-4144");
	}

	@Test
	void keepsAPersonalIdentityNumberUsedBySoleTraders() {
		assertThat(normalize("6603153138")).isEqualTo("660315-3138");
		assertThat(normalize("660315-3138")).isEqualTo("660315-3138");
	}

	@Test
	void keepsAnythingThatIsNotTenDigitsAsTyped() {
		assertThat(normalize(" 12345 ")).isEqualTo("12345");
		assertThat(normalize("not a number")).isEqualTo("not a number");
	}
}
