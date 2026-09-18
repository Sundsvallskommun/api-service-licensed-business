-- Invented data for the application tests. No real addresses, companies or restaurant numbers.
--
-- Addresses: Provgatan 1 exists in both 2281 and 2260, which is what proves the municipality scoping
-- of the lookup. Provgatan 3B carries a house number letter.
INSERT INTO address (id, street_address, postal_code, postal_area, municipality_id, created)
VALUES ('it-address-1', 'Provgatan 1', '852 30', 'Sundsvall', '2281', '2024-01-01 10:00:00'),
       ('it-address-2', 'Provgatan 3B', '852 30', 'Sundsvall', '2281', '2024-01-01 10:00:00'),
       ('it-address-3', 'Testvägen 12', '852 31', 'Sundsvall', '2281', '2024-01-01 10:00:00'),
       ('it-address-4', 'Provgatan 1', '852 30', 'Ånge', '2260', '2024-01-01 10:00:00');

INSERT INTO license_holder (id, org_number, name, created)
VALUES ('it-holder-1', '556600-1122', 'Testrestaurang AB', '2024-01-01 10:00:00'),
       ('it-holder-2', '556600-3344', 'Provkrogen HB', '2024-01-01 10:00:00');

-- Sequences 1, 3 and 4 are taken in 2281, so the next allocated number is 22810002.
INSERT INTO restaurant_number (id, restaurant_number, municipality_id, address_id, created)
VALUES ('it-number-1', '22810001', '2281', 'it-address-1', '2024-01-01 10:00:00'),
       ('it-number-2', '22810003', '2281', 'it-address-2', '2024-01-01 10:00:00'),
       ('it-number-3', '22810004', '2281', 'it-address-3', '2024-01-01 10:00:00'),
       ('it-number-4', '22600001', '2260', 'it-address-4', '2024-01-01 10:00:00');

-- it-number-1 is occupied at it-address-1, it-number-3 is free again at it-address-3, and
-- it-number-2 has never been assigned.
INSERT INTO restaurant_number_assignment (id, restaurant_number_id, license_holder_id, address_id, holder_name, premises_name, valid_from, valid_to, status, created, version)
VALUES ('it-assignment-1', 'it-number-1', 'it-holder-1', 'it-address-1', 'Testrestaurang AB', 'Provkrogen', '2020-01-01', NULL, 'ACTIVE', '2024-01-01 10:00:00', 0),
       ('it-assignment-2', 'it-number-3', 'it-holder-2', 'it-address-3', 'Provkrogen HB', 'Testbaren', '2019-01-01', '2019-12-31', 'ENDED', '2024-01-01 10:00:00', 0);
