package uk.ac.rhul.cs3821.mapper;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.entity.NewsArticleCache;
import uk.ac.rhul.cs3821.marketaux.MarketAuxArticle;
import uk.ac.rhul.cs3821.marketaux.MarketAuxEntity;

class MarketAuxNewsMapperTest {

  @Test
  void mapToNewsArticleCache_returnsNull_whenArticleIsNull() {
    NewsArticleCache result =
            MarketAuxNewsMapper.mapToNewsArticleCache(null, "MARKETAUX", "hash", "{}");
    assertNull(result);
  }

  @Test
  void mapToNewsArticleCache_mapsFields_andParsesPublishedAt_andSerializesEntities() {

    MarketAuxArticle article = new MarketAuxArticle(
            "uuid-1",
            "Title",
            "Desc",
            "https://example.com/a",
            "Reuters",
            "2026-02-25T12:34:56Z",
            List.of(
                    new MarketAuxEntity("aapl", "Apple", "equity", 0.5),
                    new MarketAuxEntity("TSLA", "Tesla", "equity", -0.1),
                    new MarketAuxEntity("AAPL", "Apple", "equity", 0.2)
            )
    );

    LocalDateTime before = LocalDateTime.now();

    NewsArticleCache result =
            MarketAuxNewsMapper.mapToNewsArticleCache(article, "MARKETAUX", "hash123", "{raw}");

    LocalDateTime after = LocalDateTime.now();

    assertNotNull(result);
    assertEquals("MARKETAUX", result.getProvider());
    assertEquals("https://example.com/a", result.getUrl());
    assertEquals("hash123", result.getUrlHash());
    assertEquals("Title", result.getTitle());
    assertEquals("Reuters", result.getSource());
    assertEquals(LocalDateTime.of(2026, 2, 25, 12, 34, 56), result.getPublishedAt());
    assertEquals("{raw}", result.getRawJson());

    // entities are uppercased, distinct, and wrapped with commas
    String entities = result.getEntities();
    assertNotNull(entities);
    assertTrue(entities.startsWith(","));
    assertTrue(entities.endsWith(","));
    assertTrue(entities.contains(",AAPL,"));
    assertTrue(entities.contains(",TSLA,"));

    // avg sentiment = (0.5 + -0.1 + 0.2) / 3 = 0.2 -> NEUTRAL (not > 0.2)
    assertEquals("NEUTRAL", result.getSentiment());

    assertNotNull(result.getFetchedAt());
    assertTrue(!result.getFetchedAt().isBefore(before));
    assertTrue(!result.getFetchedAt().isAfter(after));
  }

  @Test
  void mapToNewsArticleCache_setsNowPublishedAt_whenPublishedAtNull() {

    MarketAuxArticle article = new MarketAuxArticle(
            "uuid-1",
            "Title",
            "Desc",
            "https://example.com/a",
            "Reuters",
            null,
            List.of(new MarketAuxEntity("AAPL", "Apple", "equity", 0.9))
    );

    LocalDateTime before = LocalDateTime.now();

    NewsArticleCache result =
            MarketAuxNewsMapper.mapToNewsArticleCache(article, "MARKETAUX", "hash123", "{raw}");

    LocalDateTime after = LocalDateTime.now();

    assertNotNull(result.getPublishedAt());
    assertTrue(!result.getPublishedAt().isBefore(before));
    assertTrue(!result.getPublishedAt().isAfter(after));
  }

  @Test
  void mapToNewsArticleCache_setsNowPublishedAt_whenPublishedAtMalformed() {

    MarketAuxArticle article = new MarketAuxArticle(
            "uuid-1",
            "Title",
            "Desc",
            "https://example.com/a",
            "Reuters",
            "not-a-date",
            List.of(new MarketAuxEntity("AAPL", "Apple", "equity", -0.9))
    );

    LocalDateTime before = LocalDateTime.now();

    NewsArticleCache result =
            MarketAuxNewsMapper.mapToNewsArticleCache(article, "MARKETAUX", "hash123", "{raw}");

    LocalDateTime after = LocalDateTime.now();

    assertNotNull(result.getPublishedAt());
    assertTrue(!result.getPublishedAt().isBefore(before));
    assertTrue(!result.getPublishedAt().isAfter(after));

    assertEquals("NEGATIVE", result.getSentiment());
  }

  @Test
  void mapToNewsArticleCache_setsNeutralSentiment_whenEntitiesNullOrEmpty() {

    MarketAuxArticle nullEntities = new MarketAuxArticle(
            "uuid-1",
            "Title",
            "Desc",
            "https://example.com/a",
            "Reuters",
            "2026-02-25T12:34:56Z",
            null
    );

    NewsArticleCache result1 =
            MarketAuxNewsMapper.mapToNewsArticleCache(nullEntities, "MARKETAUX", "h", "{raw}");

    assertEquals("NEUTRAL", result1.getSentiment());
    assertNull(result1.getEntities());

    MarketAuxArticle emptyEntities = new MarketAuxArticle(
            "uuid-1",
            "Title",
            "Desc",
            "https://example.com/a",
            "Reuters",
            "2026-02-25T12:34:56Z",
            List.of()
    );

    NewsArticleCache result2 =
            MarketAuxNewsMapper.mapToNewsArticleCache(emptyEntities, "MARKETAUX", "h2", "{raw2}");

    assertEquals("NEUTRAL", result2.getSentiment());
    assertNull(result2.getEntities());
  }

  @Test
  void aggregateSentiment_positiveThreshold_isPositive() {

    MarketAuxArticle article = new MarketAuxArticle(
            "uuid-1",
            "Title",
            "Desc",
            "https://example.com/a",
            "Reuters",
            "2026-02-25T12:34:56Z",
            List.of(
                    new MarketAuxEntity("AAPL", "Apple", "equity", 0.21),
                    new MarketAuxEntity("TSLA", "Tesla", "equity", 0.21)
            )
    );

    NewsArticleCache result =
            MarketAuxNewsMapper.mapToNewsArticleCache(article, "MARKETAUX", "h3", "{raw3}");

    assertEquals("POSITIVE", result.getSentiment());
  }

  @Test
  void aggregateSentiment_negativeThreshold_isNegative() {

    MarketAuxArticle article = new MarketAuxArticle(
            "uuid-1",
            "Title",
            "Desc",
            "https://example.com/a",
            "Reuters",
            "2026-02-25T12:34:56Z",
            List.of(
                    new MarketAuxEntity("AAPL", "Apple", "equity", -0.21),
                    new MarketAuxEntity("TSLA", "Tesla", "equity", -0.21)
            )
    );

    NewsArticleCache result =
            MarketAuxNewsMapper.mapToNewsArticleCache(article, "MARKETAUX", "h4", "{raw4}");

    assertEquals("NEGATIVE", result.getSentiment());
  }
}