CREATE TABLE coupon_user
(
    id         BIGINT       NOT NULL PRIMARY KEY,
    code       VARCHAR(255) NOT NULL,
    coupon_id  BIGINT       NOT NULL,
    user_id    BIGINT,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupon_product_coupon_id ON coupon_user (coupon_id);
CREATE INDEX idx_coupon_product_user_id ON coupon_user (user_id);
CREATE INDEX idx_coupon_product_code ON coupon_user (code);