package se.sundsvall.licensedbusiness.api;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import se.sundsvall.licensedbusiness.api.model.ImportResult;
import se.sundsvall.licensedbusiness.service.ImportService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportResourceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private ImportService importService;

	@Test
	void importRestaurantNumbers() {
		final var file = new MockMultipartFile("file", "import.csv", "text/csv", new byte[0]);
		final var expected = new ImportResult(1, 1, 1, 1, 1, List.of());
		when(importService.importRestaurantNumbers(MUNICIPALITY_ID, file)).thenReturn(expected);

		final var importResource = new ImportResource(importService);
		final var response = importResource.importRestaurantNumbers(MUNICIPALITY_ID, file);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(expected);
		verify(importService).importRestaurantNumbers(MUNICIPALITY_ID, file);
	}
}
