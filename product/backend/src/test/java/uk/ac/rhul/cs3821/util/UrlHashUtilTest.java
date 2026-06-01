package uk.ac.rhul.cs3821.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class UrlHashUtilTest {

  @Test
  void sha256_shouldReturnNull_whenInputIsNull() {
    assertNull(UrlHashUtil.sha256(null));
  }

  @Test
  void sha256_shouldReturnNull_whenInputIsBlank() {
    assertNull(UrlHashUtil.sha256("   "));
  }

  @Test
  void sha256_shouldReturnDeterministicHash() {
    String url = "https://example.com";

    String hash1 = UrlHashUtil.sha256(url);
    String hash2 = UrlHashUtil.sha256(url);

    assertNotNull(hash1);
    assertEquals(hash1, hash2);
  }

  @Test
  void sha256_shouldMatchKnownSha256Value() {
    String url = "https://example.com";

    String expected =
            "100680ad546ce6a577f42f52df33b4cfdca756859e664b8d7de329b150d09ce9";

    assertEquals(expected, UrlHashUtil.sha256(url));
  }

  @Test
  void sha256_shouldProduceDifferentHashes_forDifferentInputs() {
    String hash1 = UrlHashUtil.sha256("https://a.com");
    String hash2 = UrlHashUtil.sha256("https://b.com");

    assertNotEquals(hash1, hash2);
  }
}