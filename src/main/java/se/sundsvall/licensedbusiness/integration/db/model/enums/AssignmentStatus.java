package se.sundsvall.licensedbusiness.integration.db.model.enums;

import java.time.LocalDate;

public enum AssignmentStatus {
	ACTIVE,
	ENDED;

	public static AssignmentStatus fromValidTo(final LocalDate validTo, final LocalDate today) {
		return (validTo == null) || !validTo.isBefore(today) ? ACTIVE : ENDED;
	}
}
