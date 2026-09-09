-- Test data for AddressRepositoryTest.
-- Municipality 2281: 15 addresses, 5 of which contain "storgatan" in varying case (case-insensitive search coverage).
-- Municipality 2260: 3 addresses, including one also named "Storgatan 1" (must not leak into 2281 results).

-- INSERT IGNORE: the same testcontainers MariaDB instance is reused across multiple Spring test
-- contexts within a run (SchemaVerificationTest, AddressRepositoryTest, ...), so this script runs
-- more than once against the same database.
INSERT IGNORE INTO address (id, street_address, postal_code, postal_area, municipality_id) VALUES
('address-1', 'Storgatan 1', '852 30', 'Sundsvall', '2281'),
('address-2', 'Storgatan 5', '852 31', 'Sundsvall', '2281'),
('address-3', 'Storgatan 12', '852 32', 'Sundsvall', '2281'),
('address-4', 'storgatan 20', '852 33', 'Sundsvall', '2281'),
('address-5', 'STORGATAN 99', '852 34', 'Sundsvall', '2281'),
('address-6', 'Kajplats 1', '851 02', 'Sundsvall', '2281'),
('address-7', 'Kajplats 2', '851 03', 'Sundsvall', '2281'),
('address-8', 'Esplanaden 3', '852 40', 'Sundsvall', '2281'),
('address-9', 'Esplanaden 7', '852 41', 'Sundsvall', '2281'),
('address-10', 'Nybrogatan 1', '852 50', 'Sundsvall', '2281'),
('address-11', 'Björnvägen 4', '863 30', 'Sundsvall', '2281'),
('address-12', 'Björnvägen 10', '863 31', 'Sundsvall', '2281'),
('address-13', 'Norra Kajen 1', '852 60', 'Sundsvall', '2281'),
('address-14', 'Norra Kajen 5', '852 61', 'Sundsvall', '2281'),
('address-15', 'Södra Allén 2', '852 70', 'Sundsvall', '2281'),
('address-16', 'Storgatan 1', '831 30', 'Östersund', '2260'),
('address-17', 'Torggatan 4', '831 31', 'Östersund', '2260'),
('address-18', 'Kyrkogatan 9', '831 32', 'Östersund', '2260');

-- Test data for RestaurantNumberRepositoryTest.
-- rn-1 (address-1): latest assignment (by valid_from) is ENDED -> available, despite an older ACTIVE row.
-- rn-2 (address-1): latest assignment is ACTIVE -> not available, despite an older ENDED row.
-- rn-3 (address-1): no assignment at all -> available.
-- rn-4 (address-6): latest assignment is ENDED, but tied to a different address -> must not leak into address-1 results.

INSERT IGNORE INTO license_holder (id, org_number, name) VALUES
('holder-1', '5566112233', 'Bolag A');

INSERT IGNORE INTO restaurant_number (id, restaurant_number, municipality_id, address_id) VALUES
('rn-1', '2001', '2281', 'address-1'),
('rn-2', '2002', '2281', 'address-1'),
('rn-3', '2003', '2281', 'address-1'),
('rn-4', '2004', '2281', 'address-6');

INSERT IGNORE INTO restaurant_number_assignment (id, restaurant_number_id, license_holder_id, holder_name, valid_from, valid_to, status) VALUES
('assignment-1', 'rn-1', 'holder-1', 'Bolag A', '2023-01-01', '2023-06-01', 'ACTIVE'),
('assignment-2', 'rn-1', 'holder-1', 'Bolag A', '2024-01-01', '2024-06-01', 'ENDED'),
('assignment-3', 'rn-2', 'holder-1', 'Bolag A', '2023-01-01', '2023-06-01', 'ENDED'),
('assignment-4', 'rn-2', 'holder-1', 'Bolag A', '2024-01-01', NULL, 'ACTIVE'),
('assignment-5', 'rn-4', 'holder-1', 'Bolag A', '2024-01-01', '2024-06-01', 'ENDED');
