package com.shokoku.shokokucache.service.strategy.none;

import com.shokoku.shokokucache.common.cache.CacheStrategy;
import com.shokoku.shokokucache.common.cache.ShokokuCacheEvict;
import com.shokoku.shokokucache.common.cache.ShokokuCachePut;
import com.shokoku.shokokucache.common.cache.ShokokuCacheable;
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
public class ItemNoneCacheService implements ItemCacheService {
  private final ItemService itemService;
  @Override
  @ShokokuCacheable(
          cacheStrategy = CacheStrategy.NONE,
          cacheName = "item",
          key = "#itemId",
          ttlSeconds = 5
  )
  public ItemResponse read(Long itemId) {
    return itemService.read(itemId);
  }

  @Override
  @ShokokuCacheable(
          cacheStrategy = CacheStrategy.NONE,
          cacheName = "itemList",
          key = "#page + ':' + #pageSize",
          ttlSeconds = 5
  )
  public ItemPageResponse readAll(Long page, Long pageSize) {
    return itemService.readAll(page, pageSize);
  }

  @Override
  @ShokokuCacheable(
          cacheStrategy = CacheStrategy.NONE,
          cacheName = "itemListInfiniteScroll",
          key = "#lastItemId + ':' + #pageSize",
          ttlSeconds = 5
  )
  public ItemPageResponse readAllInfiniteScroll(Long lastItemId, Long pageSize) {
    return itemService.readAllInfiniteScroll(lastItemId, pageSize);
  }

  @Override
  public ItemResponse create(ItemCreateRequest request) {
    return itemService.create(request);
  }

  @Override
  @ShokokuCachePut(
          cacheStrategy = CacheStrategy.NONE,
          cacheName = "item",
          key = "#itemId",
          ttlSeconds = 5
  )
  public ItemResponse update(Long itemId, ItemUpdateRequest request) {
    return itemService.update(itemId, request);
  }

  @Override
  @ShokokuCacheEvict(
          cacheStrategy = CacheStrategy.NONE,
          cacheName = "item",
          key = "#itemId"
  )
  public void delete(Long itemId) {
    itemService.delete(itemId);
  }

  @Override
  public boolean supports(CacheStrategy cacheStrategy) {
    return CacheStrategy.NONE == cacheStrategy;
  }
}
