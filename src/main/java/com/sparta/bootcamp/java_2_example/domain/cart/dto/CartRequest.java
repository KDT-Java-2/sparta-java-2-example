package com.sparta.bootcamp.java_2_example.domain.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartRequest {

  @NotNull
  Long userId;

  @NotNull
  Long productId;

  @NotNull
  Integer quantity;

}

