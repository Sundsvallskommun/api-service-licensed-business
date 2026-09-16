package se.sundsvall.licensedbusiness.service;

import java.util.regex.Pattern;

public final class TextSanitizer {

	private static final Pattern CONTROL_CHARACTERS = Pattern.compile("[\\p{Cc}\\p{Cf}\\p{Zl}\\p{Zp}]");
	private static final Pattern REPEATED_SPACES = Pattern.compile(" {2,}");

	private TextSanitizer() {}

	public static String sanitize(final String raw) {
		if (raw == null) {
			return null;
		}

		final var withoutControlCharacters = CONTROL_CHARACTERS.matcher(raw).replaceAll(" ");
		return REPEATED_SPACES.matcher(withoutControlCharacters).replaceAll(" ").trim();
	}
}
