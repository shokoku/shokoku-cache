package com.shokoku.shokokucache.service.strategy.bloomfilter;

@FunctionalInterface
public interface BloomFilterHashFunction {
  long hash(String value);
}
