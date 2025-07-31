package com.sparta.bootcamp.java_2_example.domain.product.controller;

import com.sparta.bootcamp.java_2_example.common.response.ApiResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.AddToCartRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.DisplayedProduct;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductResponse;
import com.sparta.bootcamp.java_2_example.domain.product.service.ProductSearchService;
import com.sparta.bootcamp.java_2_example.domain.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;
  private final ProductSearchService productSearchService;

  @GetMapping
  public ApiResponse<List<ProductResponse>> getAll() {
    return ApiResponse.success(productService.getAll());
  }

  @GetMapping("/{id}")
  public ApiResponse<ProductResponse> getById(@PathVariable Long id) {
    return ApiResponse.success(productService.getById(id));
  }

  @PostMapping
  public ApiResponse<Void> create(@Valid @RequestBody ProductRequest request) {
    productService.save(request);
    return ApiResponse.success();
  }

  @GetMapping("/search")
  public String search(@RequestParam String query) {
    return productSearchService.searchProducts(query);
  }

  @GetMapping("/v2/search")
  public List<DisplayedProduct> searchV2(@RequestParam String query) {
    return productSearchService.searchProductsAndSaveSession(query);
  }

  @PostMapping("/cart/add-ai")
  public List<String> addToCartByAI(@RequestBody AddToCartRequest request) {
    return productSearchService.addProductsToCartByAI(request.getCommand());
  }

}

