package com.shokoku.shokokucache.service;

import com.shokoku.shokokucache.common.cache.CacheStrategy;
import com.shokoku.shokokucache.model.ItemCreateRequest;
import com.shokoku.shokokucache.model.ItemUpdateRequest;
import com.shokoku.shokokucache.service.response.ItemPageResponse;
import com.shokoku.shokokucache.service.response.ItemResponse;

public interface ItemCacheService {
  ItemResponse read(Long itemId);
  ItemPageResponse readAll(Long page, Long pageSize);
  ItemPageResponse readAllInfiniteScroll(Long lastItemId, Long pageSize);
  ItemResponse create(ItemCreateRequest request);
  ItemResponse update(Long itemId, ItemUpdateRequest request);
  void delete(Long itemId);
  boolean supports(CacheStrategy cacheStrategy);
}
