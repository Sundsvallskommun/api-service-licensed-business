package se.sundsvall.licensedbusiness.integration.db.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

/**
 * AddressRepository tests.
 *
 * @see /src/test/resources/db/scripts/testdata.sql for data setup.
 */
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
class AddressRepositoryTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String OTHER_MUNICIPALITY_ID = "2260";

	@Autowired
	private AddressRepository addressRepository;

	@Test
	void findAllByMunicipalityId() {
		final var page = addressRepository.findAllByMunicipalityId(MUNICIPALITY_ID, PageRequest.of(0, 20));

		assertThat(page.getTotalElements()).isEqualTo(15);
		assertThat(page.getContent()).allMatch(entity -> MUNICIPALITY_ID.equals(entity.getMunicipalityId()));
	}

	@Test
	void findAllByMunicipalityIdIsPaged() {
		final var firstPage = addressRepository.findAllByMunicipalityId(MUNICIPALITY_ID, PageRequest.of(0, 10));
		final var secondPage = addressRepository.findAllByMunicipalityId(MUNICIPALITY_ID, PageRequest.of(1, 10));

		assertThat(firstPage.getTotalPages()).isEqualTo(2);
		assertThat(firstPage.getContent()).hasSize(10);
		assertThat(secondPage.getContent()).hasSize(5);
	}

	@Test
	void findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase() {
		final var page = addressRepository.findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase(MUNICIPALITY_ID, "storgatan", PageRequest.of(0, 20));

		assertThat(page.getTotalElements()).isEqualTo(5);
		assertThat(page.getContent()).extracting("id").containsExactlyInAnyOrder("address-1", "address-2", "address-3", "address-4", "address-5");
	}

	@Test
	void findAllByMunicipalityIdAndStreetAddressContainingIgnoreCaseIsCaseInsensitive() {
		final var page = addressRepository.findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase(MUNICIPALITY_ID, "STORGATAN", PageRequest.of(0, 20));

		assertThat(page.getTotalElements()).isEqualTo(5);
	}

	@Test
	void findAllByMunicipalityIdAndStreetAddressContainingIgnoreCaseExcludesOtherMunicipalities() {
		final var page = addressRepository.findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase(MUNICIPALITY_ID, "storgatan", PageRequest.of(0, 20));

		assertThat(page.getContent()).extracting("id").doesNotContain("address-16");
	}

	@Test
	void findAllByMunicipalityIdAndStreetAddressContainingIgnoreCaseMatchesOtherMunicipality() {
		final var page = addressRepository.findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase(OTHER_MUNICIPALITY_ID, "storgatan", PageRequest.of(0, 20));

		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent()).extracting("id").containsExactly("address-16");
	}

	@Test
	void findAllByMunicipalityIdAndStreetAddressContainingIgnoreCaseWithNoMatch() {
		final var page = addressRepository.findAllByMunicipalityIdAndStreetAddressContainingIgnoreCase(MUNICIPALITY_ID, "nonexistent", PageRequest.of(0, 20));

		assertThat(page.getTotalElements()).isZero();
	}
}
