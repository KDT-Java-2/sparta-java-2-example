package com.sparta.bootcamp.java_2_example.domain.purchase.service;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseCancelRequest;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseCancelResponse;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseRequest;
import com.sparta.bootcamp.java_2_example.domain.purchase.entity.Purchase;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseRepository;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

  private final PurchaseProcessService purchaseProcessService;
  private final PurchaseCancelService purchaseCancelService;

  private final UserRepository userRepository;
  private final PurchaseRepository purchaseRepository;

  @Transactional
  public Purchase purchase(PurchaseRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

    return purchaseProcessService.process(user, request.getProducts());
  }

  @Transactional
  public PurchaseCancelResponse cancel(PurchaseCancelRequest request) {
    User user = getUser(request.getUserId(), ServiceExceptionCode.NOT_FOUND_USER);
    // user 검증은 Auth 에서 수행 했다고 가정
    return purchaseCancelService.cancelPurchase(request.getPurchaseId(), request.getUserId());
  }

  public User getUser(Long userId, ServiceExceptionCode code) {
    return userRepository.findById(userId) // <- 기능
        .orElseThrow(() -> new ServiceException(code)); // 결과
  }

}