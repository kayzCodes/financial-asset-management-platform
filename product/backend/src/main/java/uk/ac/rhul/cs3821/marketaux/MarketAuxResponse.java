package uk.ac.rhul.cs3821.marketaux;

import java.util.List;

/**
 * Record representing the top-level MarketAux API response.
 * Wraps the list of returned article objects.
 */
public record MarketAuxResponse(
        List<MarketAuxArticle> data
) {
}