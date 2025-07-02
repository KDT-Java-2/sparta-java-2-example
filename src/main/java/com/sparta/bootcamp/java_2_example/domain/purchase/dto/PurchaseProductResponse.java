package com.sparta.bootcamp.java_2_example.domain.purchase.dto;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseProductResponse {

  private Long productId;

  private String productName;
  
  private Integer quantity;

  private BigDecimal price;

  private BigDecimal totalPrice;

}
