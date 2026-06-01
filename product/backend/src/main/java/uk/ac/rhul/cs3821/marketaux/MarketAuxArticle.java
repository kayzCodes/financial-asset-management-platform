package uk.ac.rhul.cs3821.marketaux;

import java.util.List;

/**
 * Record representing a raw article payload returned by MarketAux API.
 * Mirrors provider response fields before internal mapping.
 */
public record MarketAuxArticle(
        String uuid,
        String title,
        String description,
        String url,
        String source,
        String publishedAt,
        List<MarketAuxEntity> entities
) {
}