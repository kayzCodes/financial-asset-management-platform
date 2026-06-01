package uk.ac.rhul.cs3821.entity;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class NewsArticleCacheTest {

  @Test
  void gettersAndSetters_workCorrectly() {

    NewsArticleCache entity = new NewsArticleCache();

    LocalDateTime now = LocalDateTime.now();

    entity.setId(1L);
    entity.setProvider("MARKETAUX");
    entity.setUrl("https://example.com/article");
    entity.setUrlHash("hash123");
    entity.setTitle("Test Title");
    entity.setSource("Reuters");
    entity.setPublishedAt(now);
    entity.setRawJson("{json}");
    entity.setEntities(",AAPL,TSLA,");
    entity.setSentiment("POSITIVE");
    entity.setFetchedAt(now);

    assertEquals(1L, entity.getId());
    assertEquals("MARKETAUX", entity.getProvider());
    assertEquals("https://example.com/article", entity.getUrl());
    assertEquals("hash123", entity.getUrlHash());
    assertEquals("Test Title", entity.getTitle());
    assertEquals("Reuters", entity.getSource());
    assertEquals(now, entity.getPublishedAt());
    assertEquals("{json}", entity.getRawJson());
    assertEquals(",AAPL,TSLA,", entity.getEntities());
    assertEquals("POSITIVE", entity.getSentiment());
    assertEquals(now, entity.getFetchedAt());
  }

  @Test
  void noArgsConstructor_initializesWithNulls() {

    NewsArticleCache entity = new NewsArticleCache();

    assertNull(entity.getId());
    assertNull(entity.getProvider());
    assertNull(entity.getUrl());
    assertNull(entity.getUrlHash());
    assertNull(entity.getTitle());
    assertNull(entity.getSource());
    assertNull(entity.getPublishedAt());
    assertNull(entity.getRawJson());
    assertNull(entity.getEntities());
    assertNull(entity.getSentiment());
    assertNull(entity.getFetchedAt());
  }
}