package com.sparta.bootcamp.java_2_example.domain.user.controller;

import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.service.UserService;

public class UserController {

  //기존 java 방식
  //public UserService userService = new UserServiceImpl();

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }


  public void save() {
    userService.save(User.builder().build());
  }

}
