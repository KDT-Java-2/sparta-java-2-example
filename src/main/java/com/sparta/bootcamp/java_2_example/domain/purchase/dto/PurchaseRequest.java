package com.sparta.bootcamp.java_2_example.domain.purchase.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseRequest {

  @NotNull
  Long userId;

  @NotNull
  List<PurchaseProductRequest> products;

}
