package com.sparta.bootcamp.java_2_example.domain.product.service;

import com.sparta.bootcamp.java_2_example.common.enums.PurchaseStatus;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import com.sparta.bootcamp.java_2_example.domain.category.repository.CategoryRepository;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductCreateResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductSearchRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductSearchResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductUpdateRequest;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.mapper.ProductMapper;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductQueryRepository;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseProductRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductMapper productMapper;

  private final ProductRepository productRepository;
  private final ProductQueryRepository productQueryRepository;
  private final CategoryRepository categoryRepository;
  private final PurchaseProductRepository purchaseProductRepository;
  private final ProductRedisService productRedisService;

  @Transactional(readOnly = true)
  public Page<ProductSearchResponse> searchProduct(ProductSearchRequest searchRequest,
      Pageable pageable) {

    if (searchRequest.getSort() != null && !searchRequest.getSort().isEmpty()) {
      return productRedisService.getSortedProducts(
          searchRequest.getSort(),
          searchRequest.getOrder() != null ? searchRequest.getOrder() : "asc",
          searchRequest.getCategoryId(),
          searchRequest.getMinPrice(),
          searchRequest.getMaxPrice(),
          pageable
      );
    }

    return productQueryRepository.searchProduct(searchRequest, pageable);
  }

  @Transactional(readOnly = true)
  public ProductResponse getProductById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    return productMapper.toProductResponse(product);
  }

  @Transactional
  public ProductCreateResponse create(ProductRequest request) {
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));

    Product product = productRepository.save(productMapper.toProduct(request, category));

    // Redis에 상품 추가
    productRedisService.addProductToSortedSets(product);

    return ProductCreateResponse.builder()
        .productId(product.getId())
        .build();
  }

  @Transactional
  public ProductResponse update(Long productId, ProductUpdateRequest request) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    if (Objects.nonNull(request.getCategoryId())) {
      Category category = categoryRepository.findById(request.getCategoryId())
          .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));

      product.setCategory(category);
    }
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());

    Product savedProduct = productRepository.save(product);

    // Redis에 상품 업데이트
    productRedisService.addProductToSortedSets(savedProduct);

    return productMapper.toProductResponse(savedProduct);
  }

  @Transactional
  public void delete(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    boolean hasCompletedPurchase = purchaseProductRepository
        .existsByProductIdAndPurchaseStatus(id, PurchaseStatus.COMPLETED);

    if (hasCompletedPurchase) {
      throw new ServiceException(ServiceExceptionCode.CHECK_STATUS_PURCHASE);
    }

    product.setDeletedYn(true);
  }
}
