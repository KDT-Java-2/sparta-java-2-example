package com.sparta.bootcamp.java_2_example.domain.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductBatchDto {

  Long productId;

  String name;

  String categoryName;

  BigDecimal price;

  Integer stock;

  LocalDateTime createdAt;

}
