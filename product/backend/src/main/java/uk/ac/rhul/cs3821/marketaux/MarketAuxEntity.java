package uk.ac.rhul.cs3821.marketaux;

/**
 * Record representing an entity extracted from a MarketAux article.
 * Includes symbol metadata and provider sentiment score.
 */
public record MarketAuxEntity(
        String symbol,
        String name,
        String type,
        Double sentimentScore
) {
}