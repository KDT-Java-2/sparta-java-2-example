package com.sparta.bootcamp.java_2_example.domain.user.service;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserCreateRequest;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserResponse;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserSearchResponse;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserUpdateRequest;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.mapper.UserMapper;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserQueryRepository;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;

  private final UserRepository userRepository;
  private final UserQueryRepository userQueryRepository;

  @Transactional
  public Page<UserSearchResponse> searchUser() {
    return null;
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
    return null;
  }

  @Transactional
  public void create(UserCreateRequest request) {
    userRepository.save(User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .passwordHash(request.getPassword()) // TODO: 패스워드 암호화 필요
        .build());
  }
  
  @Transactional
  public void update(Long userId, UserUpdateRequest request) {
    User user = getUser(userId);

    user.setName(request.getName());
    user.setEmail(request.getEmail());

    userRepository.save(user);
  }

  @Transactional
  public void delete(Long userId) {
    userRepository.delete(getUser(userId));
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
  }


}
