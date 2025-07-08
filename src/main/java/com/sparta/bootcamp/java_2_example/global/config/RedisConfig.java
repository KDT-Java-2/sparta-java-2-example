package com.sparta.bootcamp.java_2_example.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Value("${spring.data.redis.password:}")
  private String redisPassword;

  @Bean
  public Jedis jedis() {
    Jedis jedis = new Jedis(redisHost, redisPort);

    if (redisPassword != null && !redisPassword.isEmpty()) {
      jedis.auth(redisPassword);
    }

    return jedis;
  }
}
