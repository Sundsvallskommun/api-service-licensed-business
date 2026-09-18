
    create table address (
        municipality_id varchar(6) not null,
        created DATETIME,
        postal_code varchar(10) not null,
        id varchar(36) not null,
        postal_area varchar(100),
        street_address varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table license_holder (
        created DATETIME,
        org_number varchar(13) not null,
        id varchar(36) not null,
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table restaurant_number (
        municipality_id varchar(6) not null,
        created DATETIME,
        restaurant_number varchar(20) not null,
        address_id varchar(36) not null,
        id varchar(36) not null,
        primary key (id)
    ) engine=InnoDB;

    create table restaurant_number_assignment (
        valid_from DATE not null,
        valid_to DATE,
        created DATETIME,
        modified DATETIME,
        version bigint not null,
        address_id varchar(36) not null,
        id varchar(36) not null,
        license_holder_id varchar(36) not null,
        restaurant_number_id varchar(36) not null,
        holder_name varchar(255),
        premises_name varchar(255),
        status VARCHAR(50),
        primary key (id)
    ) engine=InnoDB;

    create index IDX_ADDRESS_MUNICIPALITY_ID 
       on address (municipality_id);

    alter table if exists address 
       add constraint UK_ADDRESS_STREET_POSTAL_MUNICIPALITY unique (street_address, postal_code, municipality_id);

    alter table if exists license_holder 
       add constraint UK_LICENSE_HOLDER_ORG_NUMBER unique (org_number);

    create index IDX_RESTAURANT_NUMBER_MUNICIPALITY_ID 
       on restaurant_number (municipality_id);

    create index IDX_RESTAURANT_NUMBER_ADDRESS_ID 
       on restaurant_number (address_id);

    alter table if exists restaurant_number 
       add constraint UK_RESTAURANT_NUMBER unique (restaurant_number);

    create index IDX_ASSIGNMENT_RESTAURANT_NUMBER_ID 
       on restaurant_number_assignment (restaurant_number_id);

    create index IDX_ASSIGNMENT_LICENSE_HOLDER_ID 
       on restaurant_number_assignment (license_holder_id);

    create index IDX_ASSIGNMENT_ADDRESS_ID 
       on restaurant_number_assignment (address_id);

    create index IDX_ASSIGNMENT_STATUS_VALID_TO 
       on restaurant_number_assignment (status, valid_to);

    alter table if exists restaurant_number 
       add constraint FK_RESTAURANT_NUMBER_ADDRESS 
       foreign key (address_id) 
       references address (id);

    alter table if exists restaurant_number_assignment 
       add constraint FK_ASSIGNMENT_ADDRESS 
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
