package se.sundsvall.licensedbusiness.service;

public final class AddressNormalizer {

	// (?U) makes \s match all unicode whitespace, including U+00A0 which String.trim() and plain \s do not.
	private static final String WHITESPACE = "(?U)\\s+";
	private static final String HOUSE_NUMBER_LETTER = "(?U)(\\d)\\s+(\\p{L})$";

	private AddressNormalizer() {}

	public static String normalizeStreetAddress(final String raw) {
		return raw.replaceAll(WHITESPACE, " ").trim().replaceAll(HOUSE_NUMBER_LETTER, "$1$2");
	}

	public static String normalizePostalCode(final String raw) {
		final var digits = raw.replaceAll(WHITESPACE, "");
		return digits.matches("\\d{5}") ? digits.substring(0, 3) + " " + digits.substring(3) : digits;
	}
}
