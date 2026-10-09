package se.sundsvall.licensedbusiness.service;

import java.util.Set;

public final class OrgNumberNormalizer {

	private static final String NON_DIGITS = "\\D";
	private static final Set<String> CENTURY_PREFIXES = Set.of("16", "19", "20");
	private static final int CENTURY_PREFIX_LENGTH = 2;
	private static final int LENGTH_WITH_CENTURY = 12;
	private static final int LENGTH = 10;
	private static final int GROUP_LENGTH = 6;

	private OrgNumberNormalizer() {}

	public static String normalize(final String raw) {
		var digits = raw.replaceAll(NON_DIGITS, "");

		if ((digits.length() == LENGTH_WITH_CENTURY) && CENTURY_PREFIXES.contains(digits.substring(0, CENTURY_PREFIX_LENGTH))) {
			digits = digits.substring(CENTURY_PREFIX_LENGTH);
		}

		return digits.length() == LENGTH ? digits.substring(0, GROUP_LENGTH) + "-" + digits.substring(GROUP_LENGTH) : raw.trim();
	}
}
