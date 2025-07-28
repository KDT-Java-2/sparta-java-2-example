package com.sparta.bootcamp.java_2_example.domain.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

  Long id;

  String name;

  String description;

  BigDecimal price;

  Integer stock;

  LocalDateTime createdAt;

  ProductCategoryResponse category;

  @Getter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ProductCategoryResponse {

    Long id;
    
    String name;

  }

}
