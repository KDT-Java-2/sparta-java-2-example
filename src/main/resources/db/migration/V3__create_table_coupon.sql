CREATE TABLE coupon
(
    id                  BIGINT         NOT NULL,
    name                VARCHAR(255)   NOT NULL,
    discount_type       VARCHAR(20)    NOT NULL,
    discount_value      DECIMAL(10, 2) NOT NULL,
    min_order_amount    DECIMAL(10, 2),
    max_discount_amount DECIMAL(10, 2),
    start_date          DATETIME       NOT NULL,
    end_date            DATETIME       NOT NULL,
    usage_limit         INT                     DEFAULT 0,
    issue_count         INT            NOT NULL DEFAULT 0,
    used_count          INT            NOT NULL DEFAULT 0,
    created_at          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupons_dates ON coupon (start_date, end_date);
CREATE INDEX idx_coupons_created_at ON coupon (created_at);


CREATE TABLE coupon_product
(
    id         BIGINT   NOT NULL PRIMARY KEY,
    coupon_id  BIGINT   NOT NULL,
    product_id BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupon_product_coupon_id ON coupon_product (coupon_id);
CREATE INDEX idx_coupon_product_product_id ON coupon_product (product_id);
