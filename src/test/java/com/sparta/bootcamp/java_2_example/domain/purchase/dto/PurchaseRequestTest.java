package com.sparta.bootcamp.java_2_example.domain.purchase.dto;

import java.util.List;

public class PurchaseRequestTest {

  private Long userId;

  private List<PurchaseProductRequestTest> products;

  public PurchaseRequestTest(Long userId, List<PurchaseProductRequestTest> products) {
    this.userId = userId;
    this.products = products;
  }

  public Long getUserId() {
    return userId;
  }

  public List<PurchaseProductRequestTest> getProducts() {
    return products;
  }
}
