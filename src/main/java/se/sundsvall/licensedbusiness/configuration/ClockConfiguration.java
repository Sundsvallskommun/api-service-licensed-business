package se.sundsvall.licensedbusiness.configuration;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfiguration {

	@Bean
	Clock clock() {
		return Clock.system(ZoneId.of("Europe/Stockholm"));
	}
}
