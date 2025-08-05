package com.sparta.bootcamp.java_2_example.domain.auth.service;


import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.auth.dto.LoginRequest;
import com.sparta.bootcamp.java_2_example.domain.auth.dto.LoginResponse;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Transactional
  public LoginResponse login(HttpSession httpSession, LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new ServiceException(ServiceExceptionCode.NOT_FOUND_USER);
    }

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    httpSession.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    httpSession.setAttribute("email", loginRequest.getEmail());
    httpSession.setAttribute("userId", user.getId());

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

  public void logout() {
    SecurityContextHolder.clearContext();
  }
}
