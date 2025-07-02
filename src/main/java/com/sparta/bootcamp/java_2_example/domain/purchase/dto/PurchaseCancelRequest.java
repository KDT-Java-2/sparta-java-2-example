package com.sparta.bootcamp.java_2_example.domain.purchase.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseCancelRequest {

  Long purchaseId;

  Long userId;

}
