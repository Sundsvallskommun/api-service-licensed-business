package se.sundsvall.licensedbusiness.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Address paging and sorting parameters")
public class AddressPagingParameters extends AbstractParameterPagingAndSortingBase {
}
