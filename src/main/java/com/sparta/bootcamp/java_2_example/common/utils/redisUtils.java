package com.sparta.bootcamp.java_2_example.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Slf4j
@Service
@RequiredArgsConstructor
public class redisUtils {

  private final Jedis jedis;
  private final ObjectMapper objectMapper;

  public <T> void saveObject(String key, T object) {
    try {
      String jsonValue = objectMapper.writeValueAsString(object);

      jedis.set(key, jsonValue);
    } catch (Exception e) {
      log.error("[RedisService] saveObject {}c: {}", key, e.getMessage());
    }
  }

  public <T> void saveObject(String key, T object, int ttlInSeconds) {
    try {
      String jsonValue = objectMapper.writeValueAsString(object);

      jedis.setex(key, ttlInSeconds, jsonValue);
    } catch (Exception e) {
      log.error("[RedisService] saveObject {}c: {}", key, e.getMessage());
    }
  }

  public void setKeyTtl(String key, int ttlInSeconds) {
    try {
      jedis.expire(key, ttlInSeconds);
    } catch (Exception e) {
      log.error("[RedisService] setKeyTtl {} : {}", key, e.getMessage());
    }
  }

  public Optional<Long> getKeyTtl(String key) {
    try {
      return Optional.of(jedis.ttl(key));
    } catch (Exception e) {
      log.error("[RedisService] getKeyTtl {} : {}", key, e.getMessage());
    }
    return Optional.empty();
  }
}
