package uk.ac.rhul.cs3821.service;

import uk.ac.rhul.cs3821.marketaux.MarketAuxResponse;

/**
 * Service for interacting with the MarketAux news API.
 * Fetches news articles filtered by asset symbols.
 */
public interface MarketAuxService {

  /**
   * Fetches news articles from MarketAux filtered by comma-separated symbols.
   *
   * @param symbols comma-separated asset symbols
   * @return {@link MarketAuxResponse} containing provider articles
   */
  MarketAuxResponse fetchNewsBySymbols(String symbols);
}
