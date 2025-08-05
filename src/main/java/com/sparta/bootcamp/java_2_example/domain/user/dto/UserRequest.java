package com.sparta.bootcamp.java_2_example.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

  @NotBlank
  String name;

  @Email
  String email;

  @NotBlank
  String password;

}
