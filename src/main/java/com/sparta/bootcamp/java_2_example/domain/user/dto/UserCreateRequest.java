package com.sparta.bootcamp.java_2_example.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {

  @NotNull
  String name;

  @Email
  String email;

  @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
  String password;

}
