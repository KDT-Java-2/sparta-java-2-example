package com.sparta.bootcamp.java_2_example.domain.product.dto;

import com.sparta.bootcamp.java_2_example.common.enums.DiscountType;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDiscountResponse {

  Long productId;

  Long couponId;

  String couponName;

  DiscountType discountType;

  BigDecimal totalDiscountAmount;
}
