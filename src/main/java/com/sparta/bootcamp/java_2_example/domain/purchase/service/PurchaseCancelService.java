package com.sparta.bootcamp.java_2_example.domain.purchase.service;

import com.sparta.bootcamp.java_2_example.common.constant.Constants;
import com.sparta.bootcamp.java_2_example.common.enums.PurchaseStatus;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseCancelResponse;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseProductResponse;
import com.sparta.bootcamp.java_2_example.domain.purchase.entity.Purchase;
import com.sparta.bootcamp.java_2_example.domain.purchase.entity.PurchaseProduct;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseProductRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseCancelService {

  private final PurchaseRepository purchaseRepository;
  private final PurchaseProductRepository purchaseProductRepository;
  private final ProductRepository productRepository;

  @Transactional
  public PurchaseCancelResponse cancelPurchase(Long purchaseId, Long userId) {
    // 1. 구매 정보 조회 및 권한 확인
    Purchase purchase = purchaseRepository.findByIdAndUser_Id(purchaseId, userId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PURCHASE));

    // 2. 취소 가능 여부 확인 및 상태 변경
    validatePurchaseStatus(purchase);

    // 3. 구매 상품 목록 조회
    List<PurchaseProduct> purchaseProducts = purchaseProductRepository.findByPurchase_Id(
        purchaseId);

    // 4. 재고 복원
    restoreProductStock(purchaseProducts);

    List<PurchaseProductResponse> PurchaseProductResponses = getPurchaseProductResponses(
        purchaseProducts);

    return PurchaseCancelResponse.builder()
        .purchaseId(purchase.getId())
        .status(purchase.getStatus())
        .cancelledAt(purchase.getUpdatedAt())
        .cancelledProducts(PurchaseProductResponses)
        .message(Constants.PURCHASE_CANCEL_MESSAGE)
        .build();
  }

  private void restoreProductStock(List<PurchaseProduct> purchaseProducts) {
    for (PurchaseProduct purchaseProduct : purchaseProducts) {
      Product product = purchaseProduct.getProduct();

      // 재고 복원 (현재 재고 + 취소된 수량)
      product.increaseStock(purchaseProduct.getQuantity());

      productRepository.save(product);
    }
  }

  private void validatePurchaseStatus(Purchase purchase) {
    if (purchase.getStatus() != PurchaseStatus.PENDING) {
      throw new ServiceException(ServiceExceptionCode.CANNOT_CANCEL);
    }
    purchase.setStatus(PurchaseStatus.CANCELED); // 취소 불가능한 상태인 경우 예외 재던지기
  }

  private List<PurchaseProductResponse> getPurchaseProductResponses(
      List<PurchaseProduct> purchaseProducts) {
    return purchaseProducts.stream()
        .map((purchaseProduct) -> {
          Product product = purchaseProduct.getProduct();
          BigDecimal totalPrice = purchaseProduct.getPrice()
              .multiply(BigDecimal.valueOf(purchaseProduct.getQuantity()));

          return PurchaseProductResponse.builder()
              .productId(product.getId())
              .productName(product.getName())
              .quantity(purchaseProduct.getQuantity())
              .price(purchaseProduct.getPrice())
              .totalPrice(totalPrice)
              .build();
        }).toList();
  }
}