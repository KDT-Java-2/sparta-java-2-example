package com.sparta.bootcamp.java_2_example.domain.purchase.service;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseProductRequest;
import com.sparta.bootcamp.java_2_example.domain.purchase.entity.Purchase;
import com.sparta.bootcamp.java_2_example.domain.purchase.entity.PurchaseProduct;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseProductRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseRepository;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseProcessService {

  private final PurchaseRepository purchaseRepository;
  private final ProductRepository productRepository;
  private final PurchaseProductRepository purchaseProductRepository;
  private final UserRepository userRepository;

  @Transactional
  public Purchase process(User user, List<PurchaseProductRequest> purchaseItems) {
    // 이제 purchase 메서드는 "무엇을 하는지" 명확히 보여준다.
    Purchase purchase = createAndSavePurchase(user);
    List<PurchaseProduct> purchaseProducts = createAndProcessPurchaseProducts(purchaseItems,
        purchase);
    BigDecimal totalPrice = calculateTotalPrice(purchaseProducts);

    purchase.setTotalPrice(totalPrice);
    return purchase;
  }

  // 각 메서드는 "어떻게 하는지" 구체적인 책임을 가진다.
  private Purchase createAndSavePurchase(User user) {
    return purchaseRepository.save(Purchase.builder()
        .user(user)
        .build());
  }

  private List<PurchaseProduct> createAndProcessPurchaseProducts(
      List<PurchaseProductRequest> itemRequests, Purchase purchase) {
    List<PurchaseProduct> purchaseProducts = new ArrayList<>();

    for (PurchaseProductRequest itemRequest : itemRequests) {
      Product product = productRepository.findById(itemRequest.getProductId()).orElseThrow();

      validateStock(product, itemRequest.getQuantity());
      product.reduceStock(itemRequest.getQuantity());

      PurchaseProduct purchaseProduct = PurchaseProduct.builder()
          .product(product)
          .purchase(purchase)
          .quantity(itemRequest.getQuantity())
          .price(product.getPrice())
          .build();

      purchaseProducts.add(purchaseProduct);
    }

    purchaseProductRepository.saveAll(purchaseProducts);
    return purchaseProducts;
  }

  private void validateStock(Product product, int requestedQuantity) {
    if (requestedQuantity > product.getStock()) {
      throw new ServiceException(ServiceExceptionCode.OUT_OF_STOCK_PRODUCT);
    }
  }

  private BigDecimal calculateTotalPrice(List<PurchaseProduct> purchaseProducts) {
    return purchaseProducts.stream()
        .map(purchaseProduct -> purchaseProduct.getPrice()
            .multiply(BigDecimal.valueOf(purchaseProduct.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}
