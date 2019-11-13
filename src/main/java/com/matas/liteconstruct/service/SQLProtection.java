package com.matas.liteconstruct.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface SQLProtection {
  public static Object protectRequestObject(Object input) {
    if (input == null)
      return null;
    if (input instanceof String) {
      return ((String) input).replace("\"", "\\\"").replace("'", "''").trim();
    } else if (input instanceof Map<?, ?>) {
      return protectMap((Map<String, Object>) input);
    } else if (input instanceof List) {
      return protectList((List<Map<String, Object>>) input);
    }
    return input;
  }

  public static List<Map<String, Object>> protectList(List<Map<String, Object>> input) {
    return (input == null ? null
        : input.stream().map(x -> (Map<String, Object>) protectMap(x))
            .collect(Collectors.toList()));
  }

  public static Map<String, Object> protectMap(Map<String, Object> input) {
    return (input == null ? null
        : input.entrySet().stream().filter(x -> x.getKey() != null && x.getValue() != null)
            .collect(Collectors.toMap(x -> (String) protectRequestObject(x.getKey()),
                x -> x.getValue() == null ? x.getValue() : protectRequestObject(x.getValue()))));
  }

  public static Map<String, String> protectStringMap(Map<String, String> input) {
    if (input == null)
      return null;
    return input.entrySet().stream()
        .collect(Collectors.toMap(x -> (String) protectRequestObject(x.getKey()),
            x -> (String) protectRequestObject(x.getValue())));
  }

  public static Object protectStringTrimLower(Object input) {
    return (input instanceof String)
        ? (input == null ? null
            : ((String) input).replace("\"", "\\\"").replace("'", "''").trim().toLowerCase())
        : input;
  }
}
