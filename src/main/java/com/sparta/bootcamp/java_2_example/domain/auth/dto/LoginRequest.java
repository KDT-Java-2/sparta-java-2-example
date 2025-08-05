package com.sparta.bootcamp.java_2_example.domain.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

  String email;

  String password;

}