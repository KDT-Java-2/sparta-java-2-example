package com.sparta.bootcamp.java_2_example.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

  Long id;

  String name;

  String email;

  LocalDateTime createdAt;

  @QueryProjection
  public UserResponse(
      Long id,
      String name,
      String email,
      LocalDateTime createdAt
  ) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.createdAt = createdAt;
  }
}
