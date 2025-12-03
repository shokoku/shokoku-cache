package com.shokoku.shokokucache.service.strategy.bloomfilter;

import com.shokoku.shokokucache.common.cache.CacheStrategy;
import com.shokoku.shokokucache.model.ItemCreateRequest;
import com.shokoku.shokokucache.model.ItemUpdateRequest;
import com.shokoku.shokokucache.service.ItemCacheService;
import com.shokoku.shokokucache.service.ItemService;
import com.shokoku.shokokucache.service.response.ItemPageResponse;
import com.shokoku.shokokucache.service.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemBloomFilterCacheService implements ItemCacheService {
  private final ItemService itemService;
  private final BloomFilterRedisHandler bloomFilterRedisHandler;

  private static final BloomFilter bloomFilter = BloomFilter.create(
          "item-bloom-filter",
          1000,
          0.01
  );
  @Override
  public ItemResponse read(Long itemId) {
    boolean result = bloomFilterRedisHandler.mightContain(bloomFilter, String.valueOf(itemId));
    if (!result) {
      return null;
    }
    return itemService.read(itemId);
  }

  @Override
  public ItemPageResponse readAll(Long page, Long pageSize) {
    return itemService.readAll(page, pageSize);
  }

  @Override
  public ItemPageResponse readAllInfiniteScroll(Long lastItemId, Long pageSize) {
    return itemService.readAllInfiniteScroll(lastItemId, pageSize);
  }


  @Override
  public ItemResponse create(ItemCreateRequest request) {
    ItemResponse itemResponse = itemService.create(request);
    bloomFilterRedisHandler.add(bloomFilter, String.valueOf(itemResponse.itemId()));
    return itemResponse;
  }

  @Override
  public ItemResponse update(Long itemId, ItemUpdateRequest request) {
    return itemService.update(itemId, request);
  }

  @Override
  public void delete(Long itemId) {
    itemService.delete(itemId);
  }

  @Override
  public boolean supports(CacheStrategy cacheStrategy) {
    return CacheStrategy.BLOOM_FILTER == cacheStrategy;
  }
}
