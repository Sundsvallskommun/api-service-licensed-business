CREATE TABLE address
(
    id              VARCHAR(36)  NOT NULL,
    street_address  VARCHAR(255) NOT NULL,
    postal_code     VARCHAR(10)  NOT NULL,
    postal_area     VARCHAR(100) NULL,
    municipality_id VARCHAR(6)   NOT NULL,
    created         datetime     NULL,
    CONSTRAINT PK_ADDRESS PRIMARY KEY (id),
    CONSTRAINT UK_ADDRESS_STREET_POSTAL_MUNICIPALITY UNIQUE (street_address, postal_code, municipality_id)
);

CREATE TABLE license_holder
(
    id         VARCHAR(36)  NOT NULL,
    org_number VARCHAR(13)  NOT NULL,
    name       VARCHAR(255) NULL,
    created    datetime     NULL,
    CONSTRAINT PK_LICENSE_HOLDER PRIMARY KEY (id),
    CONSTRAINT UK_LICENSE_HOLDER_ORG_NUMBER UNIQUE (org_number)
);

CREATE TABLE restaurant_number
(
    id                VARCHAR(36) NOT NULL,
    restaurant_number VARCHAR(20) NOT NULL,
    municipality_id   VARCHAR(6)  NOT NULL,
    address_id        VARCHAR(36) NOT NULL,
    created           datetime    NULL,
    CONSTRAINT PK_RESTAURANT_NUMBER PRIMARY KEY (id),
    CONSTRAINT UK_RESTAURANT_NUMBER UNIQUE (restaurant_number)
);

CREATE TABLE restaurant_number_assignment
(
    id                    VARCHAR(36)  NOT NULL,
    restaurant_number_id VARCHAR(36)  NOT NULL,
    license_holder_id    VARCHAR(36)  NOT NULL,
    holder_name           VARCHAR(255) NULL,
    premises_name         VARCHAR(255) NULL,
    valid_from             date         NOT NULL,
    valid_to               date         NULL,
    status                 VARCHAR(50)  NULL,
    created                datetime     NULL,
    CONSTRAINT PK_RESTAURANT_NUMBER_ASSIGNMENT PRIMARY KEY (id)
);

ALTER TABLE restaurant_number
    ADD CONSTRAINT FK_RESTAURANT_NUMBER_ADDRESS FOREIGN KEY (address_id) REFERENCES address (id);

CREATE INDEX IDX_RESTAURANT_NUMBER_ADDRESS_ID ON restaurant_number (address_id);

CREATE INDEX IDX_RESTAURANT_NUMBER_MUNICIPALITY_ID ON restaurant_number (municipality_id);

CREATE INDEX IDX_ADDRESS_MUNICIPALITY_ID ON address (municipality_id);

ALTER TABLE restaurant_number_assignment
    ADD CONSTRAINT FK_ASSIGNMENT_RESTAURANT_NUMBER FOREIGN KEY (restaurant_number_id) REFERENCES restaurant_number (id);

CREATE INDEX IDX_ASSIGNMENT_RESTAURANT_NUMBER_ID ON restaurant_number_assignment (restaurant_number_id);

ALTER TABLE restaurant_number_assignment
    ADD CONSTRAINT FK_ASSIGNMENT_LICENSE_HOLDER FOREIGN KEY (license_holder_id) REFERENCES license_holder (id);

CREATE INDEX IDX_ASSIGNMENT_LICENSE_HOLDER_ID ON restaurant_number_assignment (license_holder_id);

CREATE INDEX IDX_ASSIGNMENT_VALID_TO ON restaurant_number_assignment (valid_to);
