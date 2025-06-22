CREATE TABLE purchase (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL, -- FK: 어떤 user의 주문인지 식별
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE purchase_product ( -- 단수형으로 이름 변경
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id BIGINT NOT NULL COMMENT '어떤 주문에 속하는지',
    product_id BIGINT NOT NULL COMMENT '어떤 상품인지',
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL COMMENT '주문 시점의 상품 가격',
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);