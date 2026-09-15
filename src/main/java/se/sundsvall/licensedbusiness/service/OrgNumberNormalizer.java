package se.sundsvall.licensedbusiness.service;

public final class OrgNumberNormalizer {

	private static final String NON_DIGITS = "\\D";
	private static final String CENTURY_PREFIX = "16";
	private static final int LENGTH_WITH_CENTURY = 12;
	private static final int LENGTH = 10;
	private static final int GROUP_LENGTH = 6;

	private OrgNumberNormalizer() {}

	public static String normalize(final String raw) {
		var digits = raw.replaceAll(NON_DIGITS, "");

		if ((digits.length() == LENGTH_WITH_CENTURY) && digits.startsWith(CENTURY_PREFIX)) {
			digits = digits.substring(CENTURY_PREFIX.length());
		}

		return digits.length() == LENGTH ? digits.substring(0, GROUP_LENGTH) + "-" + digits.substring(GROUP_LENGTH) : raw.trim();
	}
}
