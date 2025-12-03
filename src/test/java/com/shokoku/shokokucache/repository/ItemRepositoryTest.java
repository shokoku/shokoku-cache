package com.shokoku.shokokucache.repository;

import com.shokoku.shokokucache.model.Item;
import com.shokoku.shokokucache.model.ItemCreateRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class ItemRepositoryTest {

  ItemRepository itemRepository = new ItemRepository();

  @Test
  void readAll() {
    List<Item> items = IntStream.range(0, 3)
            .mapToObj(idx -> itemRepository.create(Item.create(new ItemCreateRequest("data" + idx))))
            .toList();

    List<Item> firestPage = itemRepository.readAll(1L, 2L);
    List<Item> secondPage = itemRepository.readAll(2L, 2L);

    assertThat(firestPage).hasSize(2);
    assertThat(firestPage.getFirst().getItemId()).isEqualTo(items.get(2).getItemId());
    assertThat(firestPage.get(1).getItemId()).isEqualTo(items.get(1).getItemId());
    assertThat(secondPage).hasSize(1);
    assertThat(secondPage.getFirst().getItemId()).isEqualTo(items.get(0).getItemId());
  }

  @Test
  void readAllInfiniteScroll() {
    List<Item> items = IntStream.range(0, 3)
            .mapToObj(idx -> itemRepository.create(Item.create(new ItemCreateRequest("data" + idx))))
            .toList();

    List<Item> firestPage = itemRepository.readAllInfiniteScroll(null, 2L);
    List<Item> secondPage = itemRepository.readAllInfiniteScroll(firestPage.getLast().getItemId(), 2L);

    assertThat(firestPage).hasSize(2);
    assertThat(firestPage.getFirst().getItemId()).isEqualTo(items.get(2).getItemId());
    assertThat(firestPage.get(1).getItemId()).isEqualTo(items.get(1).getItemId());
    assertThat(secondPage).hasSize(1);
    assertThat(secondPage.getFirst().getItemId()).isEqualTo(items.get(0).getItemId());
  }
}


