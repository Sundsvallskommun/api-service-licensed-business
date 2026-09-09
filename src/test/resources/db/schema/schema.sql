
    create table address (
        created DATETIME,
        id VARCHAR(36) not null,
        municipality_id VARCHAR(6),
        postal_area VARCHAR(100),
        postal_code VARCHAR(10),
        street_address VARCHAR(255),
        primary key (id)
    ) engine=InnoDB;

    create table license_holder (
        created DATETIME,
        id VARCHAR(36) not null,
        name VARCHAR(255),
        org_number VARCHAR(13),
        primary key (id)
    ) engine=InnoDB;

    create table restaurant_number (
        created DATETIME,
        address_id VARCHAR(36) not null,
        id VARCHAR(36) not null,
        municipality_id VARCHAR(6),
        restaurant_number VARCHAR(20),
        primary key (id)
    ) engine=InnoDB;

    create table restaurant_number_assignment (
        valid_from DATE,
        valid_to DATE,
        created DATETIME,
        holder_name VARCHAR(255),
        id VARCHAR(36) not null,
        license_holder_id VARCHAR(36) not null,
        premises_name VARCHAR(255),
        restaurant_number_id VARCHAR(36) not null,
        status VARCHAR(50),
        primary key (id)
    ) engine=InnoDB;

    create index IDX_ADDRESS_MUNICIPALITY_ID 
       on address (municipality_id);

    alter table if exists address 
       add constraint UK_ADDRESS_STREET_POSTAL_MUNICIPALITY unique (street_address, postal_code, municipality_id);

    alter table if exists license_holder 
       add constraint UK_LICENSE_HOLDER_ORG_NUMBER unique (org_number);

    create index IDX_RESTAURANT_NUMBER_ADDRESS_ID 
       on restaurant_number (address_id);

    create index IDX_RESTAURANT_NUMBER_MUNICIPALITY_ID 
       on restaurant_number (municipality_id);

    alter table if exists restaurant_number 
       add constraint UK_RESTAURANT_NUMBER unique (restaurant_number);

    create index IDX_ASSIGNMENT_RESTAURANT_NUMBER_ID 
       on restaurant_number_assignment (restaurant_number_id);

    create index IDX_ASSIGNMENT_LICENSE_HOLDER_ID 
       on restaurant_number_assignment (license_holder_id);

    create index IDX_ASSIGNMENT_VALID_TO 
       on restaurant_number_assignment (valid_to);

    alter table if exists restaurant_number 
       add constraint FK_RESTAURANT_NUMBER_ADDRESS 
       foreign key (address_id) 
       references address (id);

    alter table if exists restaurant_number_assignment 
       add constraint FK_ASSIGNMENT_LICENSE_HOLDER 
       foreign key (license_holder_id) 
       references license_holder (id);

    alter table if exists restaurant_number_assignment 
       add constraint FK_ASSIGNMENT_RESTAURANT_NUMBER 
       foreign key (restaurant_number_id) 
       references restaurant_number (id);
