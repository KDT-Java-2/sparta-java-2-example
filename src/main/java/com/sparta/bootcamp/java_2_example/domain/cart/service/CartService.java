package com.sparta.bootcamp.java_2_example.domain.cart.service;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.cart.dto.CartRequest;
import com.sparta.bootcamp.java_2_example.domain.cart.dto.CartResponse;
import com.sparta.bootcamp.java_2_example.domain.cart.dto.CartResponse.CartItem;
import com.sparta.bootcamp.java_2_example.domain.cart.entity.Cart;
import com.sparta.bootcamp.java_2_example.domain.cart.repository.CartRepository;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  @Transactional(readOnly = true)
  public CartResponse getCartByUserId(Long userId) {
    List<Cart> userCarts = cartRepository.findByUser_Id(userId);

    List<CartItem> cartItems = userCarts.stream()
        .map(this::convertToCartItem)
        .toList();

    BigDecimal totalPaymentPrice = cartItems.stream()
        .map(CartItem::getPaymentPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return CartResponse.builder()
        .carts(cartItems)
        .totalPaymentPrice(totalPaymentPrice)
        .build();
  }

  private CartItem convertToCartItem(Cart cart) {
    Product product = cart.getProduct();
    BigDecimal price = product.getPrice();
    BigDecimal quantity = BigDecimal.valueOf(cart.getQuantity());

    return CartItem.builder()
        .productId(product.getId())
        .productName(product.getName())
        .quantity(cart.getQuantity())
        .price(price)
        .paymentPrice(price.multiply(quantity))
        .build();
  }

  @Transactional
  public void create(CartRequest request) {
    Optional<Cart> cartOptional = cartRepository.findByUser_IdAndProduct_Id(request.getUserId(),
        request.getProductId());

    if (cartOptional.isPresent()) {
      cartOptional.get().increaseQuantity(request.getQuantity());
      cartRepository.save(cartOptional.get());
      return;
    }

    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    Cart cart = Cart.builder()
        .user(user)
        .product(product)
        .quantity(request.getQuantity())
        .build();
    cartRepository.save(cart);
  }

  @Transactional
  public void delete(Long cartId) {
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CART));

    cartRepository.delete(cart);
  }
}
