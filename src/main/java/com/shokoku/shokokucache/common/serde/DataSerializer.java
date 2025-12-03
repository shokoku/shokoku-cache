package com.shokoku.shokokucache.common.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Final Utility Class
 */
@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DataSerializer {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String serializeOrException(Object data) {
    try {
      return objectMapper.writeValueAsString(data);
    }catch (Exception e) {
      log.error("DataSerializer serializeOrException error data={}", data, e);
      throw new RuntimeException(e);
    }
  }
  public static <T> T deserializeOrNull(String data, Class<T> clazz) {
    try {
      return objectMapper.readValue(data, clazz);
    }catch (Exception e) {
      log.error("DataSerializer deserializeOrException error data={}, clazz={}", data, clazz, e);
      return null;
    }
  }
}
