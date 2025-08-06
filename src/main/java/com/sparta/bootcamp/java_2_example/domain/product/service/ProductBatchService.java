package com.sparta.bootcamp.java_2_example.domain.product.service;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductBatchDto;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductBatchService {

  private final JdbcTemplate jdbcTemplate;

  public void createBatch(MultipartFile file) {
    List<ProductBatchDto> products = toProducts(file);

    String sql =
        "INSERT INTO product (name, description, price, stock, category_id, created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
    List<Object[]> batchArgs = new ArrayList<>();

    for (ProductBatchDto product : products) {
      Object[] args = new Object[]{
          product.getName(),
          product.getPrice(),
          product.getStock(),
          1L
      };
      batchArgs.add(args);
    }

    jdbcTemplate.batchUpdate(sql, batchArgs);
    log.info("Successfully inserted {} records", batchArgs.size());
    log.info("query: {} ", sql);
  }


  public void updateBatch(MultipartFile file) {
    List<ProductBatchDto> products = toProducts(file);

    String sql = "UPDATE product SET name = ?, description = ?, price = ?, stock = ?, category_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
    List<Object[]> batchArgs = new ArrayList<>();

    for (ProductBatchDto product : products) {
      Object[] args = new Object[]{
          product.getName(),
          product.getPrice(),
          product.getStock(),
          1L,
          product.getProductId()
      };
      batchArgs.add(args);
    }

    int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);

    int successCount = 0;
    for (int count : updateCounts) {
      if (count > 0) {
        successCount++;
      }
    }

    log.info("Successfully updated {} out of {} records", successCount, batchArgs.size());
    log.info("query: {}", sql);
  }

  private List<ProductBatchDto> toProducts(MultipartFile file) {
    if (file.isEmpty()) {
      throw new ServiceException(ServiceExceptionCode.EMPTY_CSV_FILE);
    }
    List<ProductBatchDto> products = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
      String line;
      boolean isFirstLine = true;

      while ((line = reader.readLine()) != null) {
        if (isFirstLine) {
          isFirstLine = false;
          continue;
        }

        if (line.trim().isEmpty()) {
          continue;
        }

        ProductBatchDto product = parseCSVLine(line);
        if (product != null) {
          products.add(product);
        }
      }
      return products;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private ProductBatchDto parseCSVLine(String line) {
    try {
      String[] fields = line.split(",");

      Long productId = Long.valueOf(fields[0].trim());
      String name = fields[1].trim();
      String categoryName = fields[2].trim();
      BigDecimal price = new BigDecimal(fields[3].trim());
      Integer stock = Integer.valueOf(fields[4].trim());

      return ProductBatchDto.builder()
          .productId(productId)
          .name(name)
          .categoryName(categoryName)
          .price(price)
          .stock(stock)
          .build();

    } catch (Exception e) {
      log.error("CSV 라인 파싱 실패: {} 오류: {}", line, e.getMessage());
      return null;
    }
  }
}
