package uk.ac.rhul.cs3821.util;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class DigestJsonUtilTest {

  @Test
  void toUrlHashJson_shouldReturnEmptyArray_whenNull() {
    assertEquals("[]", DigestJsonUtil.toUrlHashJson(null));
  }

  @Test
  void toUrlHashJson_shouldReturnEmptyArray_whenEmpty() {
    assertEquals("[]", DigestJsonUtil.toUrlHashJson(List.of()));
  }

  @Test
  void toUrlHashJson_shouldFilterNullAndBlank() {
    String json = DigestJsonUtil.toUrlHashJson(
            java.util.Arrays.asList("abc", null, " ", "xyz")
    );

    assertEquals("[\"abc\",\"xyz\"]", json);
  }

  @Test
  void toUrlHashJson_shouldEscapeQuotes() {
    String json = DigestJsonUtil.toUrlHashJson(
            List.of("a\"b")
    );

    assertEquals("[\"a\\\"b\"]", json);
  }

  @Test
  void parseUrlHashJson_shouldReturnEmptyList_whenNullOrBlank() {
    assertEquals(List.of(),
            DigestJsonUtil.parseUrlHashJson(null));

    assertEquals(List.of(),
            DigestJsonUtil.parseUrlHashJson("   "));
  }

  @Test
  void parseUrlHashJson_shouldReturnEmptyList_whenInvalidFormat() {
    assertEquals(List.of(),
            DigestJsonUtil.parseUrlHashJson("invalid"));

    assertEquals(List.of(),
            DigestJsonUtil.parseUrlHashJson("{abc}"));
  }

  @Test
  void parseUrlHashJson_shouldParseValidJson() {
    List<String> result =
            DigestJsonUtil.parseUrlHashJson(
                    "[\"abc\",\"xyz\"]"
            );

    assertEquals(List.of("abc", "xyz"), result);
  }

  @Test
  void shouldRoundTripCorrectly() {
    List<String> original = List.of("abc", "xyz");

    String json =
            DigestJsonUtil.toUrlHashJson(original);

    List<String> parsed =
            DigestJsonUtil.parseUrlHashJson(json);

    assertEquals(original, parsed);
  }
}