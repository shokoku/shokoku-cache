package com.shokoku.shokokucache.common.cache;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class ShokokuCacheKeyGenerator {
  private final ExpressionParser parser = new SpelExpressionParser();

  /**
   *
   * @return {cacheStrategy}:{cacheName}:{key}
   */
  public String genKey(JoinPoint joinPoint, CacheStrategy cacheStrategy, String cacheName, String keySpel) {
    EvaluationContext context = new StandardEvaluationContext();
    String[] parameterNaems = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
    Object[] args = joinPoint.getArgs();
    for (int i = 0; i < args.length; i++) {
      context.setVariable(parameterNaems[i], args[i]);
    }

    return cacheStrategy + ":" + cacheName + ":"
            + parser.parseExpression(keySpel).getValue(context, String.class);
  }
}
