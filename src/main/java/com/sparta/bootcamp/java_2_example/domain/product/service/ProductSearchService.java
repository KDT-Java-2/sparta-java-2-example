package com.sparta.bootcamp.java_2_example.domain.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bootcamp.java_2_example.domain.product.dto.DisplayedProduct;
import com.sparta.bootcamp.java_2_example.global.external.client.GeminiFeignClient;
import com.sparta.bootcamp.java_2_example.global.external.dto.GeminiDto;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {

  private final GeminiFeignClient geminiFeignClient;

  private final HttpSession httpSession;
  private final ObjectMapper objectMapper;
  private static final String PRODUCT_CONTEXT_KEY = "productsContext";

  @Value("${gemini.api.key}")
  private String apiKey;

  public String searchProducts(String userQuery) {
    String prompt = buildPromptForProductSearch(userQuery);
    GeminiDto.Request request = GeminiDto.Request.from(prompt);

    GeminiDto.Response response = geminiFeignClient.generateContent(apiKey, request);

    String rawJson = extractTextFromResponse(response);

    log.info("Gemini로부터 받은 검색 조건(JSON): {}", rawJson);

    return rawJson;
  }

  private String buildPromptForProductSearch(String userQuery) {
    return """
        당신은 커머스 상품 검색 전문가입니다. 사용자의 문장을 분석해서 상품을 검색하는데 필요한 핵심 속성을 JSON 형식으로 추출해야 합니다.
        - 반드시 JSON 형식으로만 응답해야 하며, 다른 어떤 설명도 추가하지 마세요.
        - 추출할 수 없는 속성은 null로 표시합니다.
        - 사용자의 검색어에서 '카테고리(category)', '색상(color)', '주요 특징(attributes)'을 추출해주세요. 특징은 배열 형태여야 합니다.
        
        사용자 검색어: "%s"
        
        JSON 출력:
        """.formatted(userQuery);
  }

  private String extractTextFromResponse(GeminiDto.Response response) {
    if (response == null || response.getCandidates() == null || response.getCandidates()
        .isEmpty()) {
      return "{}";
    }

    GeminiDto.ResponseContent content = response.getCandidates().get(0).getContent();
    if (content == null || content.getParts() == null || content.getParts().isEmpty()) {
      return "{}";
    }

    String rawText = content.getParts().get(0).getText();
    return rawText.replace("```json", "").replace("```", "").trim();
  }


  public List<DisplayedProduct> searchProductsAndSaveSession(String userQuery) {
    try {
      String searchConditions = extractSearchConditionsFromQuery(userQuery);
      log.info("Gemini로부터 받은 검색 조건: {}", searchConditions);

      List<DisplayedProduct> foundProducts = findProductsInDatabase(searchConditions);

      httpSession.setAttribute(PRODUCT_CONTEXT_KEY, foundProducts);
      log.info("사용자 세션 [{}]에 {}개 상품을 저장했습니다.",
          httpSession.getId(), foundProducts.size());

      return foundProducts;

    } catch (Exception e) {
      log.error("상품 검색 중 오류 발생", e);
      return List.of();
    }
  }

  public List<String> addProductsToCartByAI(String userCommand) {
    try {
      @SuppressWarnings("unchecked")
      List<DisplayedProduct> productsContext =
          (List<DisplayedProduct>) httpSession.getAttribute(PRODUCT_CONTEXT_KEY);

      if (productsContext == null || productsContext.isEmpty()) {
        throw new IllegalStateException("장바구니에 담을 상품 목록이 없습니다. 먼저 상품을 검색해주세요.");
      }

      String prompt = buildPromptForAddToCart(userCommand, productsContext);
      String rawJson = callGeminiAPI(prompt);

      List<String> productIdsToAdd = parseProductIds(rawJson);

      log.info("사용자의 장바구니에 {}개 상품을 추가합니다: {}", productIdsToAdd.size(), productIdsToAdd);

      return productIdsToAdd;

    } catch (Exception e) {
      log.error("장바구니 추가 처리 중 오류 발생", e);
      return List.of();
    }
  }

  private String extractSearchConditionsFromQuery(String userQuery) {
    String prompt = buildPromptForProductSearch(userQuery);
    return callGeminiAPI(prompt);
  }

  private String callGeminiAPI(String prompt) {
    try {
      GeminiDto.Request request = GeminiDto.Request.from(prompt);
      GeminiDto.Response response = geminiFeignClient.generateContent(apiKey, request);
      return extractTextFromResponse(response);
    } catch (Exception e) {
      log.error("Gemini API 호출 실패", e);
      return "{}";
    }
  }

  private List<DisplayedProduct> findProductsInDatabase(String query) {
    log.info("DB에서 '{}' 관련 상품 조회", query);

    // TODO : 상품 검색 로직 추가 필요.

    return List.of(
        new DisplayedProduct("prod-101", "삼성전자 갤럭시북 4 프로 16인치"),
        new DisplayedProduct("prod-202", "LG 그램 프로 2024 17인치"),
        new DisplayedProduct("prod-305", "Apple MacBook Air 15 M3")
    );
  }

  private String buildPromptForAddToCart(String userCommand, List<DisplayedProduct> products) {
    String productList = products.stream()
        .map(p -> "- ID: " + p.getProductId() + ", 이름: " + p.getProductName())
        .collect(Collectors.joining("\n"));

    return """
        당신은 사용자의 명령을 해석하여 장바구니에 담을 상품을 식별하는 전문가입니다.
        현재 사용자에게는 다음과 같은 상품 목록이 보여지고 있습니다.
        
        [상품 목록]
        %s
        
        위 상품 목록을 참고하여 아래 사용자 명령을 분석하고, 장바구니에 추가해야 할 상품의 'productId'만 추출하여 JSON 배열 형식으로 응답해주세요.
        - 반드시 JSON 배열 형식으로만 응답해야 합니다. 예: ["prod-101", "prod-305"]
        - 추가할 상품이 없으면 빈 배열 []을 반환하세요.
        
        [사용자 명령]
        "%s"
        
        JSON 출력:
        """.formatted(productList, userCommand);
  }

  private List<String> parseProductIds(String json) {
    try {
      return objectMapper.readValue(json, new TypeReference<List<String>>() {
      });
    } catch (JsonProcessingException e) {
      log.error("JSON 파싱 실패: {}", json, e);
      return List.of();
    }
  }
}
