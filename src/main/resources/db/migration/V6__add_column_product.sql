ALTER TABLE product
    ADD COLUMN external_yn TINYINT DEFAULT false;


ALTER TABLE product
    ADD COLUMN external_id TINYINT DEFAULT NULL;