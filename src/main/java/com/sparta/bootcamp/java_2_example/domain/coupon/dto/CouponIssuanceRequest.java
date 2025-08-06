package com.sparta.bootcamp.java_2_example.domain.coupon.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CouponIssuanceRequest {

  Long userId;

  String couponCode;

}
