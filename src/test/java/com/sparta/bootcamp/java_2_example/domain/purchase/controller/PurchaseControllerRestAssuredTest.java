package com.sparta.bootcamp.java_2_example.domain.purchase.controller;

import static org.hamcrest.core.IsEqual.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PurchaseControllerRestAssuredTest {

  @LocalServerPort
  private int port; // 실행된 서버의 포트 번호를 주입받음

  @BeforeEach
  void setUp() {
    // 모든 테스트 실행 전, Rest Assured가 요청을 보낼 포트를 설정
    RestAssured.port = port;
  }

  @Test
  void testCreatePurchase_Success() {
    // given: 요청 Body 준비
    String requestBody = """
        {
            "userId": 1,
            "products": [
                {
                    "productId": 1,
                    "quantity": 5
                }
            ]
        }
        """;

    // when & then
    RestAssured.given().log().all()                 // (요청 로깅)
        .contentType(ContentType.JSON)            // 요청 헤더의 Content-Type 설정
        .body(requestBody)                        // 요청 Body 데이터 추가
        .when()
        .post("/api/purchases")                     // POST 요청 실행
        .then().log().all()                         // (응답 로깅)
        .statusCode(200)                        // 응답 상태 코드가 201 Created 인지 검증
        .body("result", equalTo(true));      // 응답 Body의 'result' 필드 값이 true인지 검증
  }


  @Test
  void testCreatePurchase_Fail_MissingUserId() {
    // given: userId가 없는 요청 Body
    String requestBody = """
        {
            "userId": null,
            "products": [
                {
                    "productId": 1,
                    "quantity": 5
                }
            ]
        }
        """;

    // when & then
    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post("/api/purchases")
        .then()
        .statusCode(400) // 400 Bad Request 검증
        .body("error.errorCode", equalTo("VALIDATE_ERROR"));
  }

  @Test
  void testCreatePurchase_Fail_InsufficientStock() {
    // given: 재고보다 많은 수량을 주문하는 요청 Body
    String requestBody = """
        {
            "userId": 1,
            "products": [
                {
                    "productId": 1,
                    "quantity": 100000
                }
            ]
        }
        """;

    // when & then
    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post("/api/purchases")
        .then()
        .statusCode(200)
        .body("error.errorCode", equalTo("OUT_OF_STOCK_PRODUCT"));
  }


}
