package se.sundsvall.licensedbusiness.api.model;

import java.util.List;

public record ImportResult(int rowsProcessed, int addressesCreated, int licenseHoldersCreated, int restaurantNumbersCreated, int assignmentsCreated, List<String> errors) {
}
