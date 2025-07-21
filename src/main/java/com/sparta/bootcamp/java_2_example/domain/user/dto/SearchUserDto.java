package com.sparta.bootcamp.java_2_example.domain.user.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchUserDto {

  Long id;

  String name;

  String email;

  String passwordHash;

  LocalDateTime createdAt;
  
  LocalDateTime updatedAt;


}
