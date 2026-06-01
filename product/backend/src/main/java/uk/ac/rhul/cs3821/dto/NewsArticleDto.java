package uk.ac.rhul.cs3821.dto;

import java.util.List;

/**
 * DTO representing a normalized news article returned to clients.
 * Contains metadata, summary content, related asset symbols, and sentiment.
 */
public record NewsArticleDto(
        String title,
        String url,
        String source,
        String publishedAt,
        String summary,
        List<String> relatedSymbols,
        String sentiment
) {
}