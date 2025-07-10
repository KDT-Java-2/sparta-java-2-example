package com.sparta.bootcamp.java_2_example;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@SpringBootTest
public class RedisDataStructureTest {

  private static final Logger log = LoggerFactory.getLogger(RedisDataStructureTest.class);

  @Autowired
  private Jedis jedis;

  @Test
  void redisStringExample() {
    log.info("--- Redis String (문자열) 예제 시작 ---");

    // 데이터 저장: user:123:session 이라는 키에 "active" 라는 문자열 값 저장
    jedis.set("user:123:session", "active");
    log.info("데이터 저장: user:123:session -> active");

    // 데이터 조회: user:123:session 키의 값 조회
    String sessionStatus = jedis.get("user:123:session");
    log.info("Session Status 조회: {}", sessionStatus); // 예상 출력: Session Status 조회: active

    // 데이터 만료 시간 설정: user:123:session 키를 3600초(1시간) 후 만료되도록 설정
    // 캐싱된 데이터가 너무 오래되지 않도록 TTL을 설정하는 것이 중요합니다.
    jedis.expire("user:123:session", 3600); // 초 단위로 설정

    Long ttl = jedis.ttl("user:123:session");
    log.info("Session TTL (남은 시간): {} 초", ttl);

    // 숫자형 String 값 증가/감소 예제 (게시물 조회수 등)
    jedis.set("article:101:views", "0"); // 초기 조회수 0 설정
    jedis.incr("article:101:views");    // 1 증가
    jedis.incr("article:101:views");    // 1 증가
    log.info("Article 101 Views: {}", jedis.get("article:101:views")); // 예상 출력: 2

  }

  @Test
  void redisListExample() {
    log.info("--- Redis List (리스트) 예제 시작 ---");

    // 이전 실행의 잔여 데이터 클린업 (선택 사항)
    jedis.del("queue:tasks");
    log.info("이전 'queue:tasks' 데이터 삭제.");

    // 1. 작업 대기열에 작업 추가 (lpush: 왼쪽(머리)에 추가)
    // lpush "tasks" "Task1" -> [Task1]
    // lpush "tasks" "Task2" -> [Task2, Task1]
    // lpush "tasks" "Task3" -> [Task3, Task2, Task1]
    jedis.lpush("queue:tasks", "Task1", "Task2", "Task3");
    log.info("작업 대기열에 Task1, Task2, Task3 추가 (lpush)");
    log.info("현재 대기열 상태 (오른쪽에서부터 읽으면): {}", jedis.lrange("queue:tasks", 0, -1));

    // 2. 대기열 길이 확인
    Long queueSize = jedis.llen("queue:tasks");
    log.info("Queue Size (길이): {}", queueSize); // 예상 출력: Queue Size: 3

    // 3. 작업 처리 (큐에서 꺼내기 - rpop: 오른쪽(꼬리)에서 꺼내기 = FIFO)
    // rpop은 리스트의 가장 "오래된" (가장 먼저 들어간) 요소를 꺼냅니다.
    String task = jedis.rpop("queue:tasks");
    log.info("처리 중인 작업 (rpop): {}",
        task); // 예상 출력: Processing Task: Task1 (lpush로 제일 나중에 넣은 Task3 다음으로 Task2, Task1이 저장된 형태이므로 rpop하면 Task1이 나옴)

    // 4. 대기열 남은 작업 확인
    List<String> remainingTasks = jedis.lrange("queue:tasks", 0, -1); // 인덱스 0부터 끝까지 조회
    log.info("남은 작업: {}", remainingTasks); // 예상 출력: 남은 작업: [Task3, Task2]

    // 5. 다시 작업 처리
    task = jedis.rpop("queue:tasks");
    log.info("처리 중인 작업 (rpop): {}", task); // 예상 출력: Task2
    log.info("남은 작업: {}", jedis.lrange("queue:tasks", 0, -1)); // 예상 출력: [Task3]

  }

  @Test
  void redisHashExample() {
    log.info("--- Redis Hash (해시) 예제 시작 ---");

    // 이전 실행의 잔여 데이터 클린업 (선택 사항)
    jedis.del("user:123");
    log.info("이전 'user:123' 데이터 삭제.");

    // 1. 사용자 정보 저장 (user:123 이라는 키 아래에 name, email, age 필드와 값 저장)
    jedis.hset("user:123", "name", "John Doe");
    jedis.hset("user:123", "email", "john.doe@example.com");
    jedis.hset("user:123", "age", "30");
    jedis.hset("user:123", "city", "New York");
    log.info("사용자 ID 123의 정보 저장 완료.");

    // 2. 사용자 정보 조회: 특정 필드(name)의 값 조회
    String name = jedis.hget("user:123", "name");
    log.info("User Name (이름): {}", name); // 예상 출력: User Name: John Doe

    // 3. 모든 사용자 정보 조회: user:123 키의 모든 필드-값 쌍을 Map 형태로 조회
    Map<String, String> userInfo = jedis.hgetAll("user:123");
    log.info("User Info (모든 정보): {}", userInfo);
    // 예상 출력: User Info: {name=John Doe, email=john.doe@example.com, age=30, city=New York}

    // 4. 특정 필드 값 업데이트
    jedis.hset("user:123", "age", "31");
    log.info("User Age 업데이트: {}", jedis.hget("user:123", "age")); // 예상 출력: 31

  }

  @Test
  void redisSortedSetExample() {
    log.info("--- Redis Sorted Set (정렬된 집합) 예제 시작 ---");

    // 이전 실행의 잔여 데이터 클린업 (선택 사항)
    jedis.del("leaderboard");
    log.info("이전 'leaderboard' 데이터 삭제.");

    // 1. 게임 순위 저장 (zadd: 멤버와 점수 함께 추가)
    // 점수를 기준으로 오름차순으로 자동 정렬됩니다.
    jedis.zadd("leaderboard", 1500, "Player1"); // Player1의 점수: 1500
    jedis.zadd("leaderboard", 2000, "Player2"); // Player2의 점수: 2000
    jedis.zadd("leaderboard", 1200, "Player3"); // Player3의 점수: 1200
    jedis.zadd("leaderboard", 1800, "Player4"); // Player4의 점수: 1800
    log.info("게임 순위 저장 완료: Player1(1500), Player2(2000), Player3(1200), Player4(1800)");

    // 2. 상위 2명의 순위 조회 (zrevrange: 점수 내림차순으로 특정 범위 조회)
    // zrevrange(key, start_index, end_index) -> 인덱스는 0부터 시작
    // 여기서는 점수가 높은 순서대로 0번째(가장 높은 점수)부터 1번째까지를 조회합니다.
    List<String> topPlayers = jedis.zrevrange("leaderboard", 0, 1);
    log.info("상위 2명 플레이어: {}", topPlayers); // 예상 출력: [Player2, Player4] (점수: 2000, 1800)

    // 3. 특정 플레이어의 현재 점수 조회
    Double scorePlayer1 = jedis.zscore("leaderboard", "Player1");
    log.info("Player1의 점수: {}", scorePlayer1); // 예상 출력: 1500.0

    // 4. 플레이어의 점수 업데이트 (zincrby: 점수를 증가시키고 순위 자동 업데이트)
    jedis.zincrby("leaderboard", 300, "Player1"); // Player1의 점수를 300 증가 (1500 -> 1800)
    log.info("Player1의 점수 300점 증가 후: {}", jedis.zscore("leaderboard", "Player1")); // 예상 출력: 1800.0

    // 5. 점수 업데이트 후 상위 2명 다시 조회
    topPlayers = jedis.zrevrange("leaderboard", 0, 1);
    log.info("점수 업데이트 후 상위 2명 플레이어: {}",
        topPlayers);

    List<String> allPlayersSorted = jedis.zrange("leaderboard", 0, -1);
    log.info("모든 플레이어 오름차순 (점수, 이름순): {}",
        allPlayersSorted); // [Player3, Player1, Player4, Player2]
  }

}
