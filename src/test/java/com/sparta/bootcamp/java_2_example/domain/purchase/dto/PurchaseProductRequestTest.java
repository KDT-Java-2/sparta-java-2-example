package com.sparta.bootcamp.java_2_example.domain.purchase.dto;

public class PurchaseProductRequestTest {

  private Long productId;

  private Integer quantity;

  public PurchaseProductRequestTest(Long productId, Integer quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }

  public Long getProductId() {
    return productId;
  }

  public Integer getQuantity() {
    return quantity;
  }
}
