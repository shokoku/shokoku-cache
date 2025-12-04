package com.shokoku.shokokucache.service.strategy.splitbloomfilter;

import com.shokoku.shokokucache.service.strategy.bloomfilter.BloomFilter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SplitBloomFilter {
  private String id;
  private BloomFilter bloomFilter;
  private long splitCount;

//  public static final long BIT_SPLIT_UNIT = 1L << 32;
public static final long BIT_SPLIT_UNIT = 1L << 10; // 2^10 == 1024

  public static SplitBloomFilter create(String id, long dataCount, double falsePositiveRate) {
    BloomFilter bloomFilter = BloomFilter.create(id, dataCount, falsePositiveRate);
    // 비트 사이즈 1024 라면 (1024 -1) / 1024 + 1 == 1개의 Split
    // 비트 사이즈 1025 라면 (1025 -1) / 1024 + 1 == 2개의 Split
    long splitCount = (bloomFilter.getBitSize() - 1) / BIT_SPLIT_UNIT + 1;

    SplitBloomFilter splitBloomFilter = new SplitBloomFilter();
    splitBloomFilter.id = id;
    splitBloomFilter.bloomFilter = bloomFilter;
    splitBloomFilter.splitCount = splitCount;
    return splitBloomFilter;
  }

  public long findSplitIndex(Long hashedIndex) {
    // 1023라면 0번째 스플릿에 존재
    // 1024라면 1번째 스플릿에 존재
    if (hashedIndex >= bloomFilter.getBitSize()) {
      throw new IllegalArgumentException("hashedIndex out of range");
    }
    return hashedIndex / BIT_SPLIT_UNIT;
  }

  public long calSplitBitSize(long splitIndex) {
    if (splitIndex == splitCount -1) {
      // bitSize 1025, splitCount 2, splitIndex 1
      // 1025 - (1024 * 1) == 1 가장 마지막에 있는 스프릿을 다루고 있는 비트 사이즈
      return bloomFilter.getBitSize() - (BIT_SPLIT_UNIT * splitIndex);
    }
    return BIT_SPLIT_UNIT;
  }

}
