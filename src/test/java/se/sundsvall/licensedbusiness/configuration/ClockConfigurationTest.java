package se.sundsvall.licensedbusiness.configuration;

import java.time.ZoneId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClockConfigurationTest {

	@Test
	void clockUsesSwedishTime() {
		assertThat(new ClockConfiguration().clock().getZone()).isEqualTo(ZoneId.of("Europe/Stockholm"));
	}
}
