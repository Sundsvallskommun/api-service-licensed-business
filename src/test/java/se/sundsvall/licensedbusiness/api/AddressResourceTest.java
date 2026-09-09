package se.sundsvall.licensedbusiness.api;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import se.sundsvall.dept44.problem.Problem;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ADDRESS_ID = "123e4567-e89b-12d3-a456-426614174000";

	private final AddressResource addressResource = new AddressResource();

	@Test
	void getAddresses() {
		assertThatThrownBy(() -> addressResource.getAddresses(MUNICIPALITY_ID, Pageable.unpaged()))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}

	@Test
	void getAddress() {
		assertThatThrownBy(() -> addressResource.getAddress(MUNICIPALITY_ID, ADDRESS_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}

	@Test
	void searchAddresses() {
		assertThatThrownBy(() -> addressResource.searchAddresses(MUNICIPALITY_ID, "Storgatan", Pageable.unpaged()))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}

	@Test
	void createAddress() {
		assertThatThrownBy(() -> addressResource.createAddress(MUNICIPALITY_ID))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not yet implemented");
	}
}
