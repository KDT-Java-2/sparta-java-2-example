package com.sparta.bootcamp.java_2_example.domain.product.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchRequest {

  Long categoryId;
  Integer minPrice;
  Integer maxPrice;
  String sort; // "price", "createdAt", "rating"
  String order; // "asc", "desc"

}
