package com.sparta.bootcamp.java_2_example.domain.user.service;

import com.sparta.bootcamp.java_2_example.domain.user.dto.UserCreateResponse;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserRequest;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.mapper.UserMapper;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  @Transactional
  public UserCreateResponse save(UserRequest request) {
    User user = userRepository.save(User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .build());
    return userMapper.toCreateResponse(user);
  }

}
