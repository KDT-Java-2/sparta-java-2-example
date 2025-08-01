package com.sparta.bootcamp.java_2_example.global.config;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

  @NotNull
  @Override
  protected Object determineCurrentLookupKey() {
    String threadLocalKey = DataSourceContextHolder.getDataSourceKey();
    if (threadLocalKey != null) {
      log.info("Using ThreadLocal datasource key: {}", threadLocalKey);
      return threadLocalKey;
    }

    boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
    String key = isReadOnly ? "slave" : "master";
    log.info("Transaction ReadOnly: {}, Using: {}", isReadOnly, key);
    return key;
  }
}