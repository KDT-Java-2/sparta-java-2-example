CREATE TABLE coupons
(
    id                  BIGINT         NOT NULL PRIMARY KEY COMMENT '쿠폰 고유 ID (UUID)',
    name                VARCHAR(255)   NOT NULL COMMENT '쿠폰명',
    discount_type       VARCHAR(20)    NOT NULL COMMENT '할인 타입 (정률/정액)',
    discount_value      DECIMAL(10, 2) NOT NULL COMMENT '할인율 또는 할인 금액',
    min_order_amount    DECIMAL(10, 2) COMMENT '최소 주문 금액',
    max_discount_amount DECIMAL(10, 2) COMMENT '최대 할인 금액 (정률 할인 시 적용)',
    start_date          DATETIME       NOT NULL COMMENT '쿠폰 사용 시작일',
    end_date            DATETIME       NOT NULL COMMENT '쿠폰 사용 종료일',
    usage_limit         INT                     DEFAULT 0 COMMENT '총 사용 가능 횟수',
    issue_count         INT            NOT NULL DEFAULT 0 COMMENT '발행된 쿠폰 수',
    used_count          INT            NOT NULL DEFAULT 0 COMMENT '사용된 쿠폰 수',
    created_at          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시'
);

CREATE INDEX idx_coupons_dates ON coupons (start_date, end_date);
CREATE INDEX idx_coupons_created_at ON coupons (created_at);