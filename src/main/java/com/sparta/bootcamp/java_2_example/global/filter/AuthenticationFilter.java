package com.sparta.bootcamp.java_2_example.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bootcamp.java_2_example.common.response.ApiResponse;
import com.sparta.bootcamp.java_2_example.domain.auth.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();
    if (isAuthenticationRequired(requestURI)) {
      try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {

          HttpSession session = request.getSession(false);
          if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            String email = (String) session.getAttribute("email");

            if (!ObjectUtils.isEmpty(userId) && !ObjectUtils.isEmpty(email)) {
              log.info("Session authentication found for user: {}", email);
            } else {
              sendUnauthorizedResponse(response, "Authentication required");
              return;
            }
          } else {
            sendUnauthorizedResponse(response, "Authentication required");
            return;
          }
        } else {
          if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            log.info("Security authentication found for user: {}", userDetails.getUsername());
          }
        }
      } catch (Exception e) {
        log.error("Authentication filter error", e);
        sendUnauthorizedResponse(response, "Authentication failed");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private boolean isAuthenticationRequired(String requestURI) {
    String[] excludePaths = {
        "/api/auth/login",
        "/api/auth/logout",
        "/api/users",
        "/api/users/availability",
        "/swagger-ui",
        "/v3/api-docs",
        "/actuator",
        "/public",
        "/api"
    };

    for (String excludePath : excludePaths) {
      if (requestURI.startsWith(excludePath)) {
        return false;
      }
    }

    return true;
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, String message)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
        .result(false)
        .error(ApiResponse.Error.of("UNAUTHORIZED", message))
        .build();

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
