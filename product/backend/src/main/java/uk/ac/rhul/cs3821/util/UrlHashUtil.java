package uk.ac.rhul.cs3821.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Utility class for URL hashing.
 */
public final class UrlHashUtil {

  private UrlHashUtil() {
  }

  /**
   * Generates a SHA-256 hash for a URL.
   *
   * @param url the URL to hash
   * @return SHA-256 hash string
   */
  public static String sha256(String url) {
    if (url == null || url.isBlank()) {
      return null;
    }
    return DigestUtils.sha256Hex(url);
  }
}