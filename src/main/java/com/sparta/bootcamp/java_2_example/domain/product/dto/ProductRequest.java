package com.sparta.bootcamp.java_2_example.domain.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {

  @NotNull
  Long categoryId;

  @NotBlank
  String name;

  String description;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
  BigDecimal price;

  @NotNull
  @Min(value = 0, message = "재고는 0 이상이어야 합니다")
  Integer stock;

}
