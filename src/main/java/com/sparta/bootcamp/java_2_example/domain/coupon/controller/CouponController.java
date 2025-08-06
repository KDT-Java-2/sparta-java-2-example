package com.sparta.bootcamp.java_2_example.domain.coupon.controller;

import com.sparta.bootcamp.java_2_example.common.response.ApiResponse;
import com.sparta.bootcamp.java_2_example.domain.coupon.dto.CouponBatchRequest;
import com.sparta.bootcamp.java_2_example.domain.coupon.dto.CouponIssuanceRequest;
import com.sparta.bootcamp.java_2_example.domain.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

  private final CouponService couponService;

  @PostMapping("/batch")
  public ApiResponse<Void> batchCoupon(@Valid @RequestBody CouponBatchRequest request) {
    couponService.createBatchCoupon(request);
    return ApiResponse.success();
  }

  @PostMapping("/issuance")
  public ApiResponse<Void> issuanceCoupon(@Valid @RequestBody CouponIssuanceRequest request) {
    couponService.issuanceCoupon(request);
    return ApiResponse.success();
  }

}
