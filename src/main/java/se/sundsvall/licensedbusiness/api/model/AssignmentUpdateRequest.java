package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Request model for updating an assignment. Omitted fields are left unchanged, which also means "
	+ "an existing validTo cannot be cleared here. Status is recalculated from validTo. To change restaurant number, "
	+ "address or license holder, create a new assignment instead.")
public class AssignmentUpdateRequest {

	@Schema(description = "Last day the assignment is valid", examples = "2026-12-31")
	private LocalDate validTo;

	@Schema(description = "Name of the premises", examples = "Harrys Pub")
	private String premisesName;

	@Pattern(regexp = ".*\\S.*", message = "must not be blank")
	@Schema(description = "Name of the license holder as registered for this assignment, omit to leave it unchanged", examples = "Restaurang i Sundsvall AB")
	private String holderName;

	public static AssignmentUpdateRequest create() {
		return new AssignmentUpdateRequest();
	}

	public LocalDate getValidTo() {
		return validTo;
	}

	public void setValidTo(LocalDate validTo) {
		this.validTo = validTo;
	}

	public AssignmentUpdateRequest withValidTo(LocalDate validTo) {
		this.validTo = validTo;
		return this;
	}

	public String getPremisesName() {
		return premisesName;
	}

	public void setPremisesName(String premisesName) {
		this.premisesName = premisesName;
	}

	public AssignmentUpdateRequest withPremisesName(String premisesName) {
		this.premisesName = premisesName;
		return this;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public AssignmentUpdateRequest withHolderName(String holderName) {
		this.holderName = holderName;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		AssignmentUpdateRequest that = (AssignmentUpdateRequest) o;
		return Objects.equals(validTo, that.validTo) && Objects.equals(premisesName, that.premisesName) && Objects.equals(holderName, that.holderName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(validTo, premisesName, holderName);
	}

	@Override
	public String toString() {
		return "AssignmentUpdateRequest{" +
			"validTo=" + validTo +
			", premisesName='" + premisesName + '\'' +
			", holderName='" + holderName + '\'' +
			'}';
	}
}
