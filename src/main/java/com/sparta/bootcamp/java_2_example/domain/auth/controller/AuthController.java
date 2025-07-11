package com.sparta.bootcamp.java_2_example.domain.auth.controller;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.common.response.ApiResponse;
import com.sparta.bootcamp.java_2_example.domain.auth.dto.CustomUserDetails;
import com.sparta.bootcamp.java_2_example.domain.auth.dto.LoginRequest;
import com.sparta.bootcamp.java_2_example.domain.auth.dto.LoginResponse;
import com.sparta.bootcamp.java_2_example.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(HttpSession httpSession,
      @Valid @RequestBody LoginRequest loginRequest) {
    LoginResponse loginResponse = authService.login(loginRequest);

    // 세션에 사용자 정보 저장
    httpSession.setAttribute("userId", loginResponse.getUserId());
    httpSession.setAttribute("email", loginResponse.getEmail());

    log.info("session id : {}", httpSession.getId());

    return ApiResponse.success(loginResponse);
  }

  @GetMapping("/status")
  public ApiResponse<LoginResponse> checkStatus(HttpSession httpSession) {
    // Spring Security 인증 정보 확인 (필터에서 이미 검증됨)
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated() &&
        !"anonymousUser".equals(authentication.getPrincipal())) {

      // Spring Security 인증 정보에서 사용자 정보 추출
      String email = authentication.getName();
      if (authentication.getPrincipal() instanceof CustomUserDetails) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ApiResponse.success(
            authService.getLoginResponse(userDetails.getUser().getId(), email));
      }
    }

    // 세션에서 사용자 정보 확인 (필터에서 이미 검증됨)
    Long userId = (Long) httpSession.getAttribute("userId");
    String email = (String) httpSession.getAttribute("email");

    if (ObjectUtils.isEmpty(userId) && ObjectUtils.isEmpty(email)) {
      throw new ServiceException(ServiceExceptionCode.NOT_FOUND_USER);
    }

    return ApiResponse.success(authService.getLoginResponse(userId, email));
  }

  @GetMapping("/logout")
  public ApiResponse<Void> logout(HttpSession httpSession) {
    // Spring Security 컨텍스트 클리어
    authService.logout();

    // 세션 무효화
    httpSession.invalidate();

    return ApiResponse.success();
  }
}
