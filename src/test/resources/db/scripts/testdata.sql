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
