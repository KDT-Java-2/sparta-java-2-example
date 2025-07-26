package com.sparta.bootcamp.java_2_example.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum TaskStatus {
  PENDING,
  PROCESSING,
  COMPLETED,
  ;
}