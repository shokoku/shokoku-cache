package com.shokoku.shokokucache.service.strategy.splitshardedbloomfilter;

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
public class ItemSplitShardedBloomFilterCacheService implements ItemCacheService {
  private final ItemService itemService;
  private final SplitShardedBloomFilterRedisHandler splitShardedBloomFilterRedisHandler;

  private static final SplitShardedBloomFilter bloomfiler = SplitShardedBloomFilter.create(
          "item-bloom-filter",
          1000,
          0.01,
          4
  );

  @Override
  public ItemResponse read(Long itemId) {
    boolean result = splitShardedBloomFilterRedisHandler.mightContain(bloomfiler, String.valueOf(itemId));
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
    splitShardedBloomFilterRedisHandler.add(bloomfiler, String.valueOf(itemResponse.itemId()));
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
    return CacheStrategy.SPLIT_SHARDED_BLOOM_FILTER ==  cacheStrategy;
  }
}
