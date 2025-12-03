package com.shokoku.shokokucache.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ShokokuCacheAspect {
  private final List<ShokokuCacheHandler> shokokuCacheHandler;
  private final ShokokuCacheKeyGenerator shokokuCacheKeyGenerator;

  @Around("@annotation(shokokuCacheable)")
  public Object handleCacheable(ProceedingJoinPoint joinPoint, ShokokuCacheable shokokuCacheable) {
    CacheStrategy cacheStrategy = shokokuCacheable.cacheStrategy();
    ShokokuCacheHandler cacheHandler = findCacheHandler(cacheStrategy);

    String key = shokokuCacheKeyGenerator.genKey(joinPoint, cacheStrategy, shokokuCacheable.cacheName(), shokokuCacheable.key());
    Duration ttl = Duration.ofSeconds(shokokuCacheable.ttlSeconds());
    Supplier<Object> dataSourceSupplier = createDataSourceSupplier(joinPoint);
    Class returnType = findReturnType(joinPoint);

    try {
      log.info("[ShokokuCacheAspect.handleCacheable]key={}", key);
      return cacheHandler.fetch(
              key,
              ttl,
              dataSourceSupplier,
              returnType
      );
    } catch (Exception e) {
      log.error("[ShokokuCacheAspect.handleCacheable]key={}", key, e);
      return dataSourceSupplier.get();
    }
  }

  private ShokokuCacheHandler findCacheHandler(CacheStrategy cacheStrategy) {
    return shokokuCacheHandler.stream()
            .filter(handler -> handler.supports(cacheStrategy))
            .findFirst()
            .orElseThrow();
  }

  private Supplier<Object> createDataSourceSupplier(ProceedingJoinPoint joinPoint) {
    return () -> {
      try {
        return joinPoint.proceed();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  private Class findReturnType(JoinPoint joinPoint) {
    Signature signature = joinPoint.getSignature();
    MethodSignature methodSignature = (MethodSignature) signature;
    return methodSignature.getReturnType();
  }

  @AfterReturning(pointcut = "@annotation(shokokuCachePut)", returning = "result")
  public void handleCachePut(JoinPoint joinPoint, ShokokuCachePut shokokuCachePut, Object result) {
    CacheStrategy cacheStrategy = shokokuCachePut.cacheStrategy();
    ShokokuCacheHandler cacheHandler = findCacheHandler(cacheStrategy);
    String key = shokokuCacheKeyGenerator.genKey(joinPoint, cacheStrategy, shokokuCachePut.cacheName(), shokokuCachePut.key());
    log.info("[ShokokuCacheAspect.handleCachePut]key={}", key);
    cacheHandler.put(key, Duration.ofSeconds(shokokuCachePut.ttlSeconds()), result);
  }

  @AfterReturning(pointcut = "@annotation(shokokuCacheEvict)")
  public void handleCacheEvict(JoinPoint joinPoint, ShokokuCacheEvict shokokuCacheEvict) {
    CacheStrategy cacheStrategy = shokokuCacheEvict.cacheStrategy();
    ShokokuCacheHandler cacheHandler = findCacheHandler(cacheStrategy);
    String key = shokokuCacheKeyGenerator.genKey(joinPoint, cacheStrategy, shokokuCacheEvict.cacheName(), shokokuCacheEvict.key());
    log.info("[ShokokuCacheAspect.handleCacheEvict]key={}", key);
    cacheHandler.evict(key);
  }

}
