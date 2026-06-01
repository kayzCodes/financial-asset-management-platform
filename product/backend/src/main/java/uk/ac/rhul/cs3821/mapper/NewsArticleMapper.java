package uk.ac.rhul.cs3821.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import uk.ac.rhul.cs3821.dto.NewsArticleDto;
import uk.ac.rhul.cs3821.entity.NewsArticleCache;

/**
 * Mapper converting {@link NewsArticleCache} entities to DTO objects.
 * Handles date formatting and entity symbol parsing.
 */
public class NewsArticleMapper {

  private static final DateTimeFormatter FORMATTER =
          DateTimeFormatter.ISO_LOCAL_DATE_TIME;


  /**
   * Maps a cached article entity to {@link NewsArticleDto}.
   *
   * @param entity cached article entity
   * @return mapped DTO or null if entity is null
   */
  public static NewsArticleDto mapToNewsArticleDto(
          NewsArticleCache entity) {

    if (entity == null) {
      return null;
    }

    return new NewsArticleDto(
            entity.getTitle(),
            entity.getUrl(),
            entity.getSource(),
            formatPublishedAt(entity.getPublishedAt()),
            entity.getTitle(), // using title as summary placeholder
            parseSymbols(entity.getEntities()),
            entity.getSentiment()
    );
  }

  /**
   * Formats published timestamp using ISO local date-time.
   *
   * @param publishedAt article publish timestamp
   * @return formatted timestamp or null if absent
   */
  private static String formatPublishedAt(
          LocalDateTime publishedAt) {

    if (publishedAt == null) {
      return null;
    }

    return publishedAt.format(FORMATTER);
  }

  /**
   * Parses comma-delimited entity symbols into a list.
   *
   * @param entities stored symbol string
   * @return list of parsed symbols
   */
  private static List<String> parseSymbols(String entities) {

    if (entities == null || entities.isBlank()) {
      return List.of();
    }

    return Arrays.stream(entities.split(","))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .collect(Collectors.toList());
  }
}