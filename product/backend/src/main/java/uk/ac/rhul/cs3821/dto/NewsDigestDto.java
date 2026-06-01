package uk.ac.rhul.cs3821.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing a personalized news digest for a user.
 * Contains generation metadata and a list of news articles.
 */
public record NewsDigestDto(
        LocalDateTime generatedAt,
        LocalDateTime expiresAt,
        String modelVersion,
        List<NewsArticleDto> articles
) {
}