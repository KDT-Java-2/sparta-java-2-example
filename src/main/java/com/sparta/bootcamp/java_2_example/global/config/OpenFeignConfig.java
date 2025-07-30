package com.sparta.bootcamp.java_2_example.global.config;

import feign.Request;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenFeignConfig {

  @Bean
  public Request.Options feignOptions() {
    return new Request.Options(
        10000, TimeUnit.MILLISECONDS,
        60000, TimeUnit.MILLISECONDS,
        true
    );
  }

  @Bean
  public Retryer feignRetryer() {
    return Retryer.NEVER_RETRY;
  }
}