package com.shokoku.shokokucache.common.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisConfig {
  private final StringRedisTemplate redisTemplate;

  /* 애플리케이션 실행할때마다 레디스 저장된 데이터 초기화 */
  @PostConstruct
  public void clearRedisOnStartup() {
    redisTemplate.getConnectionFactory()
            .getConnection()
            .serverCommands()
            .flushDb();
  }
}
