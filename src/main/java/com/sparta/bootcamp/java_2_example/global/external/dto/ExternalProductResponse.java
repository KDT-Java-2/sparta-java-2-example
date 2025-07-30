package com.sparta.bootcamp.java_2_example.global.external.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalProductResponse {

  private Boolean result;
  private ExternalError error;
  private ExternalPage message;

  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ExternalError {

    private String errorCode;
    private String errorMessage;
  }

  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ExternalPage {

    private List<ExternalResponse> contents;
    private ExternalPageable pageable;
  }

  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ExternalPageable {

    private Long offset;
    private Long pageNumber;
    private Long pageSize;
    private Long pageElements;
    private Long totalPages;
    private Long totalElements;
    private boolean first;
    private boolean last;
  }

  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ExternalResponse {

    Long id;
    String name;
    String description;
    BigDecimal price;
    Integer stock;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
  }
}
