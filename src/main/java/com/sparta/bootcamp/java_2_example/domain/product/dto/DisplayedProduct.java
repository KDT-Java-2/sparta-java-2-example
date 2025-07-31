package com.sparta.bootcamp.java_2_example.domain.product.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DisplayedProduct implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String productId;
  private String productName;
}
