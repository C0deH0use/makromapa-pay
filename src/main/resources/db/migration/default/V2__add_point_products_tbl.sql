CREATE SEQUENCE IF NOT EXISTS product_seq
    INCREMENT BY 1
    MINVALUE 1000
    START WITH 1000;


CREATE TABLE product
(
    id           BIGINT DEFAULT nextval('product_seq') NOT NULL PRIMARY KEY,
    name         TEXT                                         NOT NULL,
    description  TEXT                                         NOT NULL,
    reasons      TEXT                                         NOT NULL,
    points       INT                                          NOT NULL,

    created      TIMESTAMP                                    NOT NULL,
    last_updated TIMESTAMP                                    NOT NULL
);