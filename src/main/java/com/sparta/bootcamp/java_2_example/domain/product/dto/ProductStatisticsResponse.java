package com.sparta.bootcamp.java_2_example.domain.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductStatisticsResponse {

  Long productCount;

  List<CategoryProductCount> categories;

  @Getter
  @RequiredArgsConstructor
  public static class CategoryProductCount {

    Long categoryId;

    String categoryName;

    Long productCount;

    Double averagePrice;

    @QueryProjection
    public CategoryProductCount(
        Long categoryId,
        String categoryName,
        Long productCount,
        Double averagePrice
    ) {
      this.categoryId = categoryId;
      this.categoryName = categoryName;
      this.productCount = productCount;
      this.averagePrice = averagePrice;
    }
  }
}
