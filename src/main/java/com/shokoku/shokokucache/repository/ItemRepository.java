package com.shokoku.shokokucache.repository;

import com.shokoku.shokokucache.model.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

/*
 * Data Source
 * 데이터베이스 OR 외부 API OR 무거운 연산
 */
@Slf4j
@Repository
public class ItemRepository {

  /* KEY를 기준으로 내림 차순 정렬*/
  private final ConcurrentSkipListMap<Long, Item> database = new ConcurrentSkipListMap<>(Comparator.reverseOrder());

  public Optional<Item> read(Long itemId) {
    log.info("[ItemRepository.read] itemId={}", itemId);
    return Optional.ofNullable(database.get(itemId));
  }

  public List<Item> readAll(Long page, Long pageSize) {
    log.info("[ItemRepository.readAll] page={}, pageSize={}", page, pageSize);
    return database.values().stream()
            .skip((page - 1) * pageSize)
            .limit(pageSize)
            .toList();
  }

  public List<Item> readAllInfiniteScroll(Long lastItemId, Long pageSize) {
    log.info("[ItemRepository.readAllInfiniteScroll] lastItemId={}, pageSize={}", lastItemId, pageSize);
    if (lastItemId == null) {
      return database.values().stream()
              .limit(pageSize)
              .toList();
    }
    // itemId를 기준으로 내림차순 정렬되어있기 때문에 tailMap 사용 (lastId 다음 데이터부터 접근)
    return database.tailMap(lastItemId, false).values().stream()
            .limit(pageSize)
            .toList();
  }

  public Item create(Item item) {
    log.info("[ItemRepository.create] item ={}", item);
    database.put(item.getItemId(), item);
    return item;
  }

  public Item update(Item item) {
    log.info("[ItemRepository.update] item ={}", item);
    database.put(item.getItemId(), item);
    return item;
  }

  public Item delete(Item item) {
    log.info("[ItemRepository.delete] item ={}", item);
    database.remove(item.getItemId());
    return item;
  }

  public long count() {
    log.info("[ItemRepository.count]");
    return database.size();
  }
}

