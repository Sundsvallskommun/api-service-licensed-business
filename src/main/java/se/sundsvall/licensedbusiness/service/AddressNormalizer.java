package se.sundsvall.licensedbusiness.service;

// Canonical spelling of street address and postal code, so that the same address written in different ways
// ("852 30" / "85230", double spaces, non-breaking spaces from excel exports) resolves to a single address row.
public final class AddressNormalizer {

	// (?U) makes \s match all unicode whitespace, including U+00A0 which String.trim() and plain \s do not.
	private static final String WHITESPACE = "(?U)\\s+";

	private AddressNormalizer() {}

	public static String normalizeStreetAddress(final String raw) {
		return raw.replaceAll(WHITESPACE, " ").trim();
	}

	// "852 30", "85230" and "8523 0" all become "852 30". Anything that is not five digits is kept as typed.
	public static String normalizePostalCode(final String raw) {
		final var digits = raw.replaceAll(WHITESPACE, "");
		return digits.matches("\\d{5}") ? digits.substring(0, 3) + " " + digits.substring(3) : digits;
	}
}
