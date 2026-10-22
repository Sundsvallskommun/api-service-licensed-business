package se.sundsvall.licensedbusiness.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.licensedbusiness.Application;
import se.sundsvall.licensedbusiness.api.model.ImportResult;
import se.sundsvall.licensedbusiness.service.ImportService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class ImportResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/{municipalityId}/import/restaurant-numbers";
	private static final String CONTENT = "restaurant_number\n22810001\n";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private ImportService importServiceMock;

	@Test
	void importRestaurantNumbers() {
		final var expected = new ImportResult(1, 1, 1, 1, 1, List.of(), List.of());
		final var received = new ArrayList<String>();
		when(importServiceMock.importRestaurantNumbers(eq(MUNICIPALITY_ID), any(MultipartFile.class))).thenAnswer(invocation -> {
			final MultipartFile file = invocation.getArgument(1);
			received.add(file.getOriginalFilename());
			received.add(new String(file.getBytes()));
			return expected;
		});
		final var body = new MultipartBodyBuilder();
		body.part("file", new ByteArrayResource(CONTENT.getBytes()) {
			@Override
			public String getFilename() {
				return "import.csv";
			}
		});

		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(MULTIPART_FORM_DATA)
			.bodyValue(body.build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(ImportResult.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(expected);
		assertThat(received).containsExactly("import.csv", CONTENT);
		verify(importServiceMock).importRestaurantNumbers(eq(MUNICIPALITY_ID), any(MultipartFile.class));
		verifyNoMoreInteractions(importServiceMock);
	}
}
