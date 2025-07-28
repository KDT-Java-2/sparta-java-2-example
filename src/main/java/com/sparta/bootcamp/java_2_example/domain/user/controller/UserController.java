package com.sparta.bootcamp.java_2_example.domain.user.controller;

import com.sparta.bootcamp.java_2_example.common.response.ApiResponse;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserCreateResponse;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserRequest;
import com.sparta.bootcamp.java_2_example.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/users")
public class UserController {

  private final UserService userService;

  @PostMapping
  public ApiResponse<UserCreateResponse> create(@Valid @RequestBody UserRequest request) {
    return ApiResponse.success(userService.save(request));
  }

}
