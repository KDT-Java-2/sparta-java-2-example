package com.sparta.bootcamp.java_2_example.domain.product.controller;

import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductResponse;
import com.sparta.bootcamp.java_2_example.domain.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductControllerV1 {

  private final ProductService productService;

  // 전체 상품 조회
  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAll() {
    return ResponseEntity.ok(productService.getAll());
  }

  // 단일 상품 조회
  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getById(id));
  }

  // 상품 생성
  @PostMapping
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
    // 201 Created 상태 코드와 함께 응답
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(productService.create(request));
  }

  // 상품 수정
  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> update(@PathVariable Long id,
      @Valid @RequestBody ProductRequest request) {
    return ResponseEntity.ok(productService.update(id, request));
  }

  // 상품 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    // 204 No Content 상태 코드로 응답
    return ResponseEntity.noContent().build();
  }
}
