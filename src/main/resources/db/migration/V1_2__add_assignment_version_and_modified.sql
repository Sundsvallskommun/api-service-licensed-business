ALTER TABLE restaurant_number_assignment
    ADD COLUMN version  BIGINT   NOT NULL DEFAULT 0,
    ADD COLUMN modified datetime NULL;

DROP INDEX IDX_ASSIGNMENT_VALID_TO ON restaurant_number_assignment;

CREATE INDEX IDX_ASSIGNMENT_STATUS_VALID_TO ON restaurant_number_assignment (status, valid_to);
