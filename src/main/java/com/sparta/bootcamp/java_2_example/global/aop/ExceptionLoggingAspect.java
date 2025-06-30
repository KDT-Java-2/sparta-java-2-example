package com.sparta.bootcamp.java_2_example.global.aop;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

  // Pointcut: Service 계층의 모든 메서드를 대상으로 지정
  @Pointcut("execution(* com.sparta.bootcamp.java_2_example.domain..service..*(..))")
  private void allServiceMethods() {
  }

  // Advice: serviceMethods Pointcut에서 ServiceException 타입의 예외 발생 시 동작
  @AfterThrowing(pointcut = "allServiceMethods()", throwing = "exception")
  public void logServiceException(ServiceException exception) {
    // "exception"이라는 이름으로 예외 객체를 받아옴
    log.error("Service Layer Exception: Code = [{}], Message = [{}]",
        exception.getCode(), exception.getMessage());
  }

}
