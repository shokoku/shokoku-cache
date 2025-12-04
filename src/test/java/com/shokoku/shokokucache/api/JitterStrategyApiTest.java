package com.shokoku.shokokucache.api;

import com.shokoku.shokokucache.common.cache.CacheStrategy;
import com.shokoku.shokokucache.model.ItemCreateRequest;
import com.shokoku.shokokucache.model.ItemUpdateRequest;
import com.shokoku.shokokucache.service.response.ItemResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JitterStrategyApiTest {
  static final CacheStrategy CACHE_STRATEGY = CacheStrategy.JITTER;

  @Test
  void test() throws InterruptedException {
    List<ItemResponse> items = List.of(
            ItemApiTestUtils.create(CACHE_STRATEGY, new ItemCreateRequest("data1")),
            ItemApiTestUtils.create(CACHE_STRATEGY, new ItemCreateRequest("data2")),
            ItemApiTestUtils.create(CACHE_STRATEGY, new ItemCreateRequest("data3"))
    );

    ExecutorService executorService = Executors.newFixedThreadPool(3);
    long start = System.nanoTime();
    while(System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
      for (ItemResponse item : items) {
        executorService.execute(() -> ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId()));
      }
      TimeUnit.MICROSECONDS.sleep(10);
    }

    ItemApiTestUtils.update(CACHE_STRATEGY, items.getFirst().itemId(), new ItemUpdateRequest("updated"));
    ItemResponse updated = ItemApiTestUtils.read(CACHE_STRATEGY, items.getFirst().itemId());
    System.out.println("updated = " + updated);

    ItemApiTestUtils.delete(CACHE_STRATEGY, items.getFirst().itemId());
    ItemResponse deleted = ItemApiTestUtils.read(CACHE_STRATEGY, items.getFirst().itemId());
    System.out.println("deleted = " + deleted);
  }

}
