package com.sparta.bootcamp.java_2_example.domain.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

  List<CartItem> carts;

  BigDecimal totalPaymentPrice;

  @Getter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class CartItem {

    Long productId;

    String productName;

    Integer quantity;

    BigDecimal price;

    BigDecimal paymentPrice;
  }

}
