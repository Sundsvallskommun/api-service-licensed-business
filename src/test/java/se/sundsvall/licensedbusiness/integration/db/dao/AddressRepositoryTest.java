package se.sundsvall.licensedbusiness.integration.db.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;
import static se.sundsvall.licensedbusiness.service.AddressNormalizer.normalizePostalCode;
import static se.sundsvall.licensedbusiness.service.AddressNormalizer.normalizeStreetAddress;

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

		assertThat(page.getTotalElements()).isEqualTo(16);
		assertThat(page.getContent()).allMatch(entity -> MUNICIPALITY_ID.equals(entity.getMunicipalityId()));
	}

	@Test
	void findAllByMunicipalityIdIsPaged() {
		final var firstPage = addressRepository.findAllByMunicipalityId(MUNICIPALITY_ID, PageRequest.of(0, 10));
		final var secondPage = addressRepository.findAllByMunicipalityId(MUNICIPALITY_ID, PageRequest.of(1, 10));

		assertThat(firstPage.getTotalPages()).isEqualTo(2);
		assertThat(firstPage.getContent()).hasSize(10);
		assertThat(secondPage.getContent()).hasSize(6);
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

		assertThat(page.getContent()).extracting("id").isNotEmpty().doesNotContain("address-16");
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

	@Test
	void findByStreetAddressAndPostalCodeAndMunicipalityId() {
		final var entity = addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId("Storgatan 1", "852 30", MUNICIPALITY_ID);

		assertThat(entity).get().extracting("id").isEqualTo("address-1");
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {
		"Kajplats 3B|851 04",
		"kajplats 3b|851 04",
		"KAJPLATS 3B|851 04"
	})
	void findByStreetAddressAndPostalCodeAndMunicipalityIdIsCaseInsensitive(final String streetAddress, final String postalCode) {
		final var entity = addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId(streetAddress, postalCode, MUNICIPALITY_ID);

		assertThat(entity).get().extracting("id").isEqualTo("address-19");
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {
		"Kajplats 3B|851 04",
		"Kajplats 3 B|851 04",
		"  kajplats   3 b  |85104"
	})
	void normalizedInputFindsTheStoredAddress(final String streetAddress, final String postalCode) {
		final var entity = addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId(
			normalizeStreetAddress(streetAddress), normalizePostalCode(postalCode), MUNICIPALITY_ID);

		assertThat(entity).get().extracting("id").isEqualTo("address-19");
	}

	@Test
	void findByStreetAddressAndPostalCodeAndMunicipalityIdExcludesOtherMunicipalities() {
		final var entity = addressRepository.findByStreetAddressAndPostalCodeAndMunicipalityId("Kajplats 3B", "851 04", OTHER_MUNICIPALITY_ID);

		assertThat(entity).isEmpty();
	}

	@Test
	void existsByIdAndMunicipalityId() {
		assertThat(addressRepository.existsByIdAndMunicipalityId("address-1", MUNICIPALITY_ID)).isTrue();
		assertThat(addressRepository.existsByIdAndMunicipalityId("address-1", OTHER_MUNICIPALITY_ID)).isFalse();
		assertThat(addressRepository.existsByIdAndMunicipalityId("nonexistent", MUNICIPALITY_ID)).isFalse();
	}
}
