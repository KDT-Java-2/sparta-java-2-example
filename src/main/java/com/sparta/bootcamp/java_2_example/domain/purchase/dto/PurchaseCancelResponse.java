package com.sparta.bootcamp.java_2_example.domain.purchase.dto;

import com.sparta.bootcamp.java_2_example.common.enums.PurchaseStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseCancelResponse {

  private Long purchaseId;

  private PurchaseStatus status;

  private LocalDateTime cancelledAt;

  private List<PurchaseProductResponse> cancelledProducts;

  private String message;
  
}
