package uk.ac.rhul.cs3821.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import uk.ac.rhul.cs3821.entity.NewsArticleCache;
import uk.ac.rhul.cs3821.marketaux.MarketAuxArticle;
import uk.ac.rhul.cs3821.marketaux.MarketAuxEntity;

/**
 * Mapper class for MarketAuxArticle.
 */
public class MarketAuxNewsMapper {

  /**
   * Maps a MarketAuxArticle to a NewsArticleCache entity.
   *
   * @param article  the MarketAuxArticle
   * @param provider the provider name (e.g. MARKETAUX)
   * @param urlHash  precomputed hash of article URL
   * @param rawJson  full raw JSON string
   * @return the corresponding NewsArticleCache entity
   */
  public static NewsArticleCache mapToNewsArticleCache(
          MarketAuxArticle article,
          String provider,
          String urlHash,
          String rawJson) {

    if (article == null) {
      return null;
    }

    NewsArticleCache entity = new NewsArticleCache();

    entity.setProvider(provider);
    entity.setUrl(article.url());
    entity.setUrlHash(urlHash);
    entity.setTitle(article.title());
    entity.setSource(article.source());
    entity.setPublishedAt(parsePublishedAt(article.publishedAt()));
    entity.setRawJson(rawJson);
    entity.setEntities(serializeEntities(article.entities()));
    entity.setSentiment(aggregateSentiment(article.entities()));
    entity.setFetchedAt(LocalDateTime.now());

    return entity;
  }

  /**
   * Parses provider published timestamp into LocalDateTime.
   * Returns current time if parsing fails or value is invalid.
   *
   * @param publishedAt provider timestamp string
   * @return parsed LocalDateTime or current time on failure
   */
  private static LocalDateTime parsePublishedAt(String publishedAt) {
    if (publishedAt == null || publishedAt.isBlank()) {
      return LocalDateTime.now();
    }

    try {
      return OffsetDateTime.parse(publishedAt).toLocalDateTime();
    } catch (RuntimeException ex) {
      return LocalDateTime.now();
    }
  }

  /**
   * Serializes article entities into a comma-delimited symbol string.
   * Wraps symbols with leading and trailing commas for LIKE queries.
   *
   * @param entities provider entity list
   * @return formatted symbol string or null if none
   */
  private static String serializeEntities(List<MarketAuxEntity> entities) {

    if (entities == null || entities.isEmpty()) {
      return null;
    }

    return entities.stream()
            .filter(e -> e != null && e.symbol() != null)
            .map(e -> e.symbol().toUpperCase())
            .distinct()
            .collect(Collectors.joining(",", ",", ","));
  }

  /**
   * Computes aggregate sentiment from provider entity scores.
   * Applies simple threshold logic to classify sentiment.
   *
   * @param entities provider entity list
   * @return POSITIVE, NEGATIVE, or NEUTRAL classification
   */
  private static String aggregateSentiment(List<MarketAuxEntity> entities) {
    if (entities == null || entities.isEmpty()) {
      return "NEUTRAL";
    }

    double avg = entities.stream()
            .filter(e -> e != null && e.sentimentScore() != null)
            .mapToDouble(MarketAuxEntity::sentimentScore)
            .average()
            .orElse(0.0);

    if (avg > 0.2) {
      return "POSITIVE";
    }
    if (avg < -0.2) {
      return "NEGATIVE";
    }

    return "NEUTRAL";
  }


}