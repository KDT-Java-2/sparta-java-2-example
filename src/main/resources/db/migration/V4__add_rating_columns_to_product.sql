ALTER TABLE product
    ADD COLUMN average_rating DECIMAL(3, 2) DEFAULT 0.00 NULL,
    ADD COLUMN rating_count   INT           DEFAULT 0    NULL;