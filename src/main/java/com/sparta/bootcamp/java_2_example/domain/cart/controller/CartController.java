package com.sparta.bootcamp.java_2_example.domain.cart.controller;

import com.sparta.bootcamp.java_2_example.common.response.ApiResponse;
import com.sparta.bootcamp.java_2_example.domain.cart.dto.CartRequest;
import com.sparta.bootcamp.java_2_example.domain.cart.dto.CartResponse;
import com.sparta.bootcamp.java_2_example.domain.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

  private final CartService cartService;

  @GetMapping("/users/{userId}")
  public ApiResponse<CartResponse> getCartsByUser(@PathVariable Long userId) {
    return ApiResponse.success(cartService.getCartByUserId(userId));
  }

  @PostMapping
  public ApiResponse<CartResponse> create(@RequestBody CartRequest request) {
    cartService.create(request);
    return ApiResponse.success();
  }

  @DeleteMapping("/{cartId}")
  public ApiResponse<CartResponse> delete(@PathVariable Long cartId) {
    cartService.delete(cartId);
    return ApiResponse.success();
  }

}
