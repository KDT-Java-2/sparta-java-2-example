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
  INSUFFICIENT_STOCK("상품의 재고가 부족합니다."),
  NOT_FOUND_USER("유저를 찾을 수 없습니다."),
  OUT_OF_STOCK_PRODUCT("재고 수량이 없습니다."),
  NOT_FOUND_PURCHASE("주문 내역을 확인 할 수 없습니다."),
  CANNOT_CANCEL("취소 불가능한 상태입니다."),

  // ... 다른 예외 코드들
  ;

  final String message;
}
