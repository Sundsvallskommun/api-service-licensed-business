package se.sundsvall.licensedbusiness.integration.db.model.enums;

import java.time.LocalDate;
import java.time.ZoneId;

public enum AssignmentStatus {
	ACTIVE,
	ENDED;

	public static AssignmentStatus fromValidTo(final LocalDate validTo) {
		return (validTo == null) || !validTo.isBefore(LocalDate.now(ZoneId.systemDefault())) ? ACTIVE : ENDED;
	}
}
