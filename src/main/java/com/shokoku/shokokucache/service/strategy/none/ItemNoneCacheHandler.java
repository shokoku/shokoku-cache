package com.shokoku.shokokucache.service.strategy.none;

import com.shokoku.shokokucache.common.cache.CacheStrategy;
import com.shokoku.shokokucache.common.cache.ShokokuCacheHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemNoneCacheHandler implements ShokokuCacheHandler {
  @Override
  public <T> T fetch(String key, Duration ttl, Supplier<T> dataSourceSupplier, Class<T> clazz) {
    log.info("[ItemNoneCacheHandler.fetch]key={}", key);
    return dataSourceSupplier.get();
  }

  @Override
  public void put(String key, Duration ttl, Object value) {
    log.info("[ItemNoneCacheHandler.put]key={}, value={}", key, value);
  }

  @Override
  public void evict(String key) {
    log.info("[ItemNoneCacheHandler.evict]key={}", key);

  }

  @Override
  public boolean supports(CacheStrategy cacheStrategy) {
    return CacheStrategy.NONE == cacheStrategy;
  }
}
