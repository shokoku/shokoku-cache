package com.shokoku.shokokucache.service.strategy.nullobjectpattern;

import com.shokoku.shokokucache.common.cache.CacheStrategy;
import com.shokoku.shokokucache.model.ItemCreateRequest;
import com.shokoku.shokokucache.model.ItemUpdateRequest;
import com.shokoku.shokokucache.service.ItemCacheService;
import com.shokoku.shokokucache.service.ItemService;
import com.shokoku.shokokucache.service.response.ItemPageResponse;
import com.shokoku.shokokucache.service.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemNullObjectPatternCacheService implements ItemCacheService {
  private final ItemService itemService;

  private static final ItemResponse nullObject = new ItemResponse(null, null);

  @Override
  @Cacheable(cacheNames = "item", key = "#itemId")
  public ItemResponse read(Long itemId) {
    ItemResponse itemResponse = itemService.read(itemId);
    if (itemResponse == null) {
      return nullObject;
    }
    return itemResponse;
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
    return itemService.create(request);
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
    return CacheStrategy.NULL_OBJECT_PATTERN == cacheStrategy;
  }
}
