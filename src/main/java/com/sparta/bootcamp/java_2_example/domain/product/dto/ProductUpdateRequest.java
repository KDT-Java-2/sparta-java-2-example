package com.sparta.bootcamp.java_2_example.domain.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {

  Long categoryId;

  String name;

  String description;

  @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
  BigDecimal price;

  @Min(value = 0, message = "재고는 0 이상이어야 합니다")
  Integer stock;
  
}
