package se.sundsvall.licensedbusiness.service;

import java.time.LocalDate;
import java.time.ZoneId;
import se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus;

import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ACTIVE;
import static se.sundsvall.licensedbusiness.integration.db.model.enums.AssignmentStatus.ENDED;

public final class AssignmentStatusResolver {

	private AssignmentStatusResolver() {}

	public static AssignmentStatus resolveStatus(final LocalDate validTo) {
		return (validTo == null) || !validTo.isBefore(LocalDate.now(ZoneId.systemDefault())) ? ACTIVE : ENDED;
	}
}
