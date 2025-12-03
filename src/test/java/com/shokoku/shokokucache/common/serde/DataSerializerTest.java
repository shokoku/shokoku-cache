package com.shokoku.shokokucache.common.serde;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataSerializerTest {
  @Test
  void serde() {
    MyData mydata = new MyData("id", "data");
    String serialized = DataSerializer.serializeOrException(mydata);

    MyData deserialized = DataSerializer.deserializeOrNull(serialized, MyData.class);
    assertThat(deserialized).isEqualTo(mydata);
  }

  record MyData(
          String id, String data
  ) {
  }

}
