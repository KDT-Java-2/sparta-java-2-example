package com.sparta.bootcamp.java_2_example.domain.purchase.service;

import com.sparta.bootcamp.java_2_example.common.enums.PurchaseStatus;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.cart.entity.Cart;
import com.sparta.bootcamp.java_2_example.domain.cart.repository.CartRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseCreateResponse;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseRequest;
import com.sparta.bootcamp.java_2_example.domain.purchase.entity.Purchase;
import com.sparta.bootcamp.java_2_example.domain.purchase.entity.PurchaseProduct;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseProductRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseRepository;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

  private final CartRepository cartRepository;
  private final UserRepository userRepository;
  private final PurchaseRepository purchaseRepository;
  private final PurchaseProductRepository purchaseProductRepository;

  @Transactional
  public PurchaseCreateResponse create(PurchaseRequest request) {
    List<Cart> carts = cartRepository.findByUser_Id(request.getUserId());
    if (carts.isEmpty()) {
      throw new ServiceException(ServiceExceptionCode.NOT_FOUND_CART);
    }

    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

    Purchase purchase = createPurchase(user, request.getShippingAddress());
    BigDecimal totalPrice = createPurchaseProducts(carts, purchase);
    purchase.updateTotalPrice(totalPrice);
    cartRepository.deleteAll(carts);

    return PurchaseCreateResponse.builder()
        .purchaseId(purchase.getId())
        .build();
  }

  private Purchase createPurchase(User user, String shippingAddress) {
    return purchaseRepository.save(
        Purchase.builder()
            .user(user)
            .totalPrice(BigDecimal.ZERO)
            .status(PurchaseStatus.PENDING)
            .address(shippingAddress)
            .build()
    );
  }

  private BigDecimal createPurchaseProducts(List<Cart> carts, Purchase purchase) {
    List<PurchaseProduct> purchaseProducts = carts.stream()
        .map(cart -> createPurchaseProduct(cart, purchase))
        .toList();

    purchaseProductRepository.saveAll(purchaseProducts);

    return purchaseProducts.stream()
        .map(this::calculateItemTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private PurchaseProduct createPurchaseProduct(Cart cart, Purchase purchase) {
    return PurchaseProduct.builder()
        .purchase(purchase)
        .product(cart.getProduct())
        .quantity(cart.getQuantity())
        .price(cart.getProduct().getPrice())
        .build();
  }

  private BigDecimal calculateItemTotal(PurchaseProduct purchaseProduct) {
    return purchaseProduct.getPrice()
        .multiply(BigDecimal.valueOf(purchaseProduct.getQuantity()));
  }

}
