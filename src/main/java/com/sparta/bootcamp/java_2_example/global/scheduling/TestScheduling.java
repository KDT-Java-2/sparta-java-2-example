package com.sparta.bootcamp.java_2_example.global.scheduling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestScheduling {

  @Scheduled(cron = "0 0 * * * *")
  public void batchUser() {
    log.info("run batchUser : 유저에 대한 배치 처리를 합니다.");
  }

}
