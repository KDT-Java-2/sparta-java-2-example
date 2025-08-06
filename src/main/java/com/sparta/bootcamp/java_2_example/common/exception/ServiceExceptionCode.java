package com.sparta.bootcamp.java_2_example.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode {

  NOT_FOUND_DATA("데이터를 찾을 수 없습니다"),
  NOT_FOUND_PRODUCT("상품을 찾을 수 없습니다."),
  NOT_FOUND_CATEGORY("카테고리를 찾을 수 없습니다."),
  INSUFFICIENT_STOCK("상품의 재고가 부족합니다."),
  NOT_FOUND_USER("유저를 찾을 수 없습니다."),
  OUT_OF_STOCK_PRODUCT("재고 수량이 없습니다."),
  NOT_FOUND_PURCHASE("주문 내역을 확인 할 수 없습니다."),
  CANNOT_CANCEL("취소 불가능한 상태입니다."),
  NOT_FOUND_TASK("작업을 찾을 수 없습니다."),
  CHECK_STATUS_PURCHASE("주문이 완료 된 상품이 있습니다."),
  NOT_DELETED_CATEGORY("카테고리를 제거 할 수 없습니다."),
  NOT_FOUND_CART("장바구니 정보를 찾을 수 없습니다."),
  NOT_FOUND_COUPON("쿠폰 정보가를 찾을 수 없습니다."),
  EMPTY_CSV_FILE("파일이 비어있습니다.");

  final String message;
}
