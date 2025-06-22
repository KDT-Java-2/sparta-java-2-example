package com.sparta.bootcamp.java_2_example.domain.product.service;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductResponse;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public List<ProductResponse> getAll() {
    return new ArrayList<>();
  }

  public ProductResponse getById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    return ProductResponse.builder()
        .id(product.getId())
        .categoryId(product.getCategoryId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .stock(product.getStock())
        .createdAt(product.getCreatedAt())
        .build();
  }

  public ProductResponse create(ProductRequest request) {
    return null;
  }

  public ProductResponse update(Long id, ProductRequest request) {
    return null;
  }

  public void delete(Long id) {

  }
}
