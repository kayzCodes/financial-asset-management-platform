package uk.ac.rhul.cs3821.util;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility for serializing and deserializing digest URL hash lists.
 * Produces compact JSON arrays without external dependencies.
 */
public final class DigestJsonUtil {

  private DigestJsonUtil() {
  }

  /**
   * Serializes URL hashes into a compact JSON array string.
   *
   * @param urlHashes list of URL hash values
   * @return JSON array string representation
   */
  public static String toUrlHashJson(List<String> urlHashes) {
    if (urlHashes == null || urlHashes.isEmpty()) {
      return "[]";
    }

    return "[" + urlHashes.stream()
            .filter(h -> h != null && !h.isBlank())
            .map(DigestJsonUtil::quote)
            .collect(Collectors.joining(","))
            + "]";
  }

  private static String quote(String value) {
    String escaped = value.replace("\"", "\\\"");
    return "\"" + escaped + "\"";
  }

  /**
   * Parses a JSON array string into a list of URL hashes.
   *
   * @param digestJson JSON array string
   * @return list of parsed URL hashes
   */
  public static List<String> parseUrlHashJson(String digestJson) {
    if (digestJson == null || digestJson.isBlank()) {
      return List.of();
    }

    String trimmed = digestJson.trim();
    if (trimmed.equals("[]")) {
      return List.of();
    }

    if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
      return List.of();
    }

    String inner = trimmed.substring(1, trimmed.length() - 1).trim();
    if (inner.isEmpty()) {
      return List.of();
    }

    return java.util.Arrays.stream(inner.split(","))
            .map(String::trim)
            .map(s -> s.replaceAll("^\"|\"$", ""))
            .filter(s -> !s.isBlank())
            .toList();
  }
}