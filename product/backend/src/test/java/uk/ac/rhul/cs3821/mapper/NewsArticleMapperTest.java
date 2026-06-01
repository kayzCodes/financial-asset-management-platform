package uk.ac.rhul.cs3821.mapper;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.dto.NewsArticleDto;
import uk.ac.rhul.cs3821.entity.NewsArticleCache;

class NewsArticleMapperTest {

  @Test
  void mapToNewsArticleDto_shouldReturnNull_whenEntityIsNull() {
    assertNull(NewsArticleMapper.mapToNewsArticleDto(null));
  }

  @Test
  void mapToNewsArticleDto_shouldMapAllFieldsCorrectly() {
    NewsArticleCache entity = new NewsArticleCache();
    entity.setTitle("Test Title");
    entity.setUrl("https://example.com");
    entity.setSource("Reuters");
    entity.setPublishedAt(LocalDateTime.of(2026, 2, 25, 10, 30));
    entity.setEntities(",AAPL,TSLA,");
    entity.setSentiment("POSITIVE");

    NewsArticleDto dto =
            NewsArticleMapper.mapToNewsArticleDto(entity);

    assertNotNull(dto);
    assertEquals("Test Title", dto.title());
    assertEquals("https://example.com", dto.url());
    assertEquals("Reuters", dto.source());
    assertEquals("2026-02-25T10:30:00", dto.publishedAt());
    assertEquals("Test Title", dto.summary());
    assertEquals(List.of("AAPL", "TSLA"), dto.relatedSymbols());
    assertEquals("POSITIVE", dto.sentiment());
  }

  @Test
  void mapToNewsArticleDto_shouldReturnNullPublishedAt_whenNull() {
    NewsArticleCache entity = new NewsArticleCache();
    entity.setTitle("Test");
    entity.setEntities(",AAPL,");

    NewsArticleDto dto =
            NewsArticleMapper.mapToNewsArticleDto(entity);

    assertNull(dto.publishedAt());
  }

  @Test
  void parseSymbols_shouldReturnEmptyList_whenNullOrBlank() {
    NewsArticleCache nullEntities = new NewsArticleCache();
    nullEntities.setEntities(null);

    NewsArticleCache blankEntities = new NewsArticleCache();
    blankEntities.setEntities("   ");

    assertEquals(
            List.of(),
            NewsArticleMapper.mapToNewsArticleDto(nullEntities)
                    .relatedSymbols()
    );

    assertEquals(
            List.of(),
            NewsArticleMapper.mapToNewsArticleDto(blankEntities)
                    .relatedSymbols()
    );
  }

  @Test
  void parseSymbols_shouldIgnoreEmptySegments() {
    NewsArticleCache entity = new NewsArticleCache();
    entity.setEntities(",AAPL,,TSLA, ,ETH,");

    NewsArticleDto dto =
            NewsArticleMapper.mapToNewsArticleDto(entity);

    assertEquals(
            List.of("AAPL", "TSLA", "ETH"),
            dto.relatedSymbols()
    );
  }
}