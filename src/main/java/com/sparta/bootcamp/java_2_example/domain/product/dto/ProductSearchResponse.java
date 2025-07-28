package com.sparta.bootcamp.java_2_example.domain.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchResponse {

  Long id;

  String name;

  BigDecimal price;

  Integer stock;

  LocalDateTime createdAt;

  @QueryProjection
  public ProductSearchResponse(
      Long id,
      String name,
      BigDecimal price,
      Integer stock,
      LocalDateTime createdAt
  ) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.stock = stock;
    this.createdAt = createdAt;
  }
}
