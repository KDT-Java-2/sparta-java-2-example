package com.sparta.bootcamp.java_2_example.domain.purchase.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseProductRequest;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseProductRequestTest;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseRequest;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseRequestTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PurchaseControllerTest {

  @Autowired
  private MockMvc mockMvc; // API 테스트를 위한 핵심 객체

  @Autowired
  private ObjectMapper objectMapper; // 객체를 JSON 문자열로 변환하기 위한 객체

  // 구매 생성 성공 테스트
  @Test
  void testCreatePurchase_Success() throws Exception {
    // given: 테스트에 사용할 요청 DTO와 JSON Body 준비
    List<PurchaseProductRequest> purchaseProductRequests = new ArrayList<>();

    PurchaseProductRequest purchaseProductRequest = new PurchaseProductRequest();
    ReflectionTestUtils.setField(purchaseProductRequest, "productId", 1L);
    ReflectionTestUtils.setField(purchaseProductRequest, "quantity", 10);

    purchaseProductRequests.add(purchaseProductRequest);

    PurchaseRequest request = new PurchaseRequest();
    ReflectionTestUtils.setField(request, "userId", 1L);
    ReflectionTestUtils.setField(request, "purchaseRequest", purchaseProductRequests);

    String requestBody = new ObjectMapper().writeValueAsString(request);

    // when & then: API를 호출하고 응답을 검증
    mockMvc.perform(post("/api/purchases")               // 1. HTTP POST 요청을 /api/purchases 로 보냄
            .contentType(MediaType.APPLICATION_JSON.toString())    // 2. 요청의 Content-Type을 JSON으로 설정
            .content(requestBody)                                  // 3. 요청 Body에 JSON 데이터 추가
            .accept(MediaType.APPLICATION_JSON.toString()))        // 4. 클라이언트가 JSON 응답을 기대함을 명시
        .andExpect(status().is(200))                           // 5. 응답 상태 코드가 200 Created 인지 검증
        .andExpect(jsonPath("$.result").value(true));    // 6. 응답 Body의 result 필드가 true인지 검증
  }

  @Test
  void testCreatePurchase_Fail_MissingUserId() throws Exception {
    // given: userId가 null인 요청 DTO
    List<PurchaseProductRequestTest> purchaseProductRequestTests = new ArrayList<>();
    PurchaseProductRequestTest purchaseProductRequestTest = new PurchaseProductRequestTest(1L, 10);
    purchaseProductRequestTests.add(purchaseProductRequestTest);

    PurchaseRequestTest request = new PurchaseRequestTest(10L, null);

    String requestBody = new ObjectMapper().writeValueAsString(request);

    // when & then
    mockMvc.perform(post("/api/purchases")
            .contentType(MediaType.APPLICATION_JSON.toString())
            .content(requestBody))
        .andExpect(jsonPath("$.error.errorCode").value("NOT_FOUND_USER"));
  }

  @Test
  void testCreatePurchase_Fail_InsufficientStock() throws Exception {
    // given: 재고(예: 5개)보다 많은 수량(예: 10개)을 주문하는 DTO
    List<PurchaseProductRequestTest> purchaseProductRequestTests = new ArrayList<>();
    PurchaseProductRequestTest purchaseProductRequestTest = new PurchaseProductRequestTest(1L,
        1000);
    purchaseProductRequestTests.add(purchaseProductRequestTest);

    PurchaseRequestTest request = new PurchaseRequestTest(1L, purchaseProductRequestTests);

    String requestBody = new ObjectMapper().writeValueAsString(request);

    // when & then
    mockMvc.perform(post("/api/purchases")
            .contentType(MediaType.APPLICATION_JSON.toString())
            .content(requestBody))
        .andExpect(jsonPath("$.error.errorCode").value("OUT_OF_STOCK_PRODUCT"));
  }

}