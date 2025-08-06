package com.sparta.bootcamp.java_2_example.domain.coupon.service;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.coupon.dto.CouponBatchRequest;
import com.sparta.bootcamp.java_2_example.domain.coupon.dto.CouponIssuanceRequest;
import com.sparta.bootcamp.java_2_example.domain.coupon.entity.Coupon;
import com.sparta.bootcamp.java_2_example.domain.coupon.entity.CouponUser;
import com.sparta.bootcamp.java_2_example.domain.coupon.repository.CouponRepository;
import com.sparta.bootcamp.java_2_example.domain.coupon.repository.CouponUserRepository;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;
  private final CouponUserRepository couponUserRepository;
  private final UserRepository userRepository;

  @Transactional
  public void createBatchCoupon(CouponBatchRequest request) {
    Coupon coupon = couponRepository.findById(request.getCouponId())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_COUPON));

    List<CouponUser> couponUsers = new ArrayList<>();
    for (int i = 0; i < request.getCount(); i++) {
      String couponCode = get8CharHash();
      CouponUser couponUser = CouponUser.builder()
          .code(couponCode)
          .coupon(coupon)
          .build();

      couponUsers.add(couponUser);

      if ((i + 1) % 100 == 0) {
        create(couponUsers);
        couponUsers.clear();
      }
    }

    if (!couponUsers.isEmpty()) {
      create(couponUsers);
    }
  }

  @Async
  public void create(List<CouponUser> couponUsers) {
    couponUserRepository.saveAll(couponUsers);
  }

  private String get8CharHash() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public void issuanceCoupon(CouponIssuanceRequest request) {
    CouponUser couponUser = couponUserRepository.findByCode(request.getCouponCode())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_COUPON));

    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

    couponUser.setUser(user);
  }

}
