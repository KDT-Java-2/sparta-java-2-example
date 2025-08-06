package com.sparta.bootcamp.java_2_example.domain.coupon.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CouponBatchRequest {

  Long couponId;

  Integer count;

}
