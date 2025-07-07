package com.sparta.bootcamp.java_2_example.domain.auth;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.auth.dto.LoginRequest;
import com.sparta.bootcamp.java_2_example.domain.auth.dto.LoginResponse;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;

  @Transactional
  public LoginResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

    return LoginResponse.builder()
        .userId(user.getId())
        .email(loginRequest.getEmail())
        .build();
  }

  public LoginResponse getLoginResponse(Long userId, String email) {
    return LoginResponse.builder()
        .userId(userId)
        .email(email)
        .build();
  }
}
