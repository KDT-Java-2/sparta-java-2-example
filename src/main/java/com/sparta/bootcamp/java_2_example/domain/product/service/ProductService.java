package com.sparta.bootcamp.java_2_example.domain.product.service;

import com.sparta.bootcamp.java_2_example.common.exception.CustomCheckedException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import com.sparta.bootcamp.java_2_example.domain.category.repository.CategoryRepository;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductResponse;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Transactional(readOnly = true)
  public List<ProductResponse> getAll() {
    return productRepository.findAll().stream()
        .map((product -> ProductResponse.builder()
            .id(product.getId())
            .categoryId(product.getCategory().getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .stock(product.getStock())
            .createdAt(product.getCreatedAt())
            .build()))
        .toList();
  }

  @Transactional
  public ProductResponse getById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    return ProductResponse.builder()
        .id(product.getId())
        .categoryId(product.getCategory().getId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .stock(product.getStock())
        .createdAt(product.getCreatedAt())
        .build();
  }

  @Transactional
  public void save(ProductRequest request) {
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    productRepository.save(Product.builder()
        .category(category)
        .name(request.getName())
        .description(request.getDescription())
        .price(request.getPrice())
        .stock(request.getStock())
        .build());
  }

  @Transactional(rollbackFor = CustomCheckedException.class)
  public void updateProductQuantity(Long productId, Integer quantity)
      throws CustomCheckedException {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    log.info("상품 재고를 {}에서 {}로 변경 시도.", product.getPrice(), quantity);
    product.setStock(quantity);
    productRepository.save(product); // 변경 사항을 우선 DB에 반영

    // 예외 발생 조건: 음수 가격은 허용하지 않음 (체크 예외)
    if (quantity < 0) {
      throw new CustomCheckedException("재고은 음수가 될 수 없습니다.");
    }
  }

  @Transactional(noRollbackFor = IllegalArgumentException.class)
  public void reduceProductStockNoRollback(Long productId, Integer quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    // 이 예제에서는 예외 발생 전 다른 DB 작업을 수행했다고 가정합니다.
    // ex) logRepository.save(new Log("재고 차감 시도..."));

    // 재고 부족 시 IllegalArgumentException 발생 (언체크 예외)
    if (product.getStock() < quantity) {
      throw new IllegalArgumentException("재고가 부족합니다.");
    }

    product.reduceStock(quantity);
    productRepository.save(product);
  }

}
