package com.sparta.bootcamp.java_2_example.domain.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {

  Long categoryId;

  @NotNull
  String name;

  String description;

  @NotNull
  @Positive // 값을 양수로만 제한
  BigDecimal price;

  @NotNull
  @PositiveOrZero // 값을 양수로 혹은 0으로 제한
  Integer stock;
  
}
