package com.sparta.bootcamp.java_2_example.domain.product.controller;

import com.sparta.bootcamp.java_2_example.common.response.ApiResponse;
import com.sparta.bootcamp.java_2_example.domain.product.service.ProductExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external/products")
public class ProductExternalController {

  private final ProductExternalService productExternalService;

  @PostMapping
  public ApiResponse<Void> batchCreate() {
    productExternalService.saveAllExternalProducts();
    return ApiResponse.success();
  }


}
