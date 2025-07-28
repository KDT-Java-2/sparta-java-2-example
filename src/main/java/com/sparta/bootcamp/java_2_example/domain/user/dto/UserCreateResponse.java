package com.sparta.bootcamp.java_2_example.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateResponse {

  String name;

  String email;

  String password;
}
