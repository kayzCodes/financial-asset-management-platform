package uk.ac.rhul.cs3821.service;

import uk.ac.rhul.cs3821.dto.StockFundamentalsDto;

/**
 * Service for managing stock fundamental data.
 */
public interface StockFundamentalService {

  /**
   * Retrieves stock fundamentals for a given symbol.
   * Checks the database first, then falls back to Alpha Vantage if missing or stale.
   *
   * @param symbol the stock ticker symbol (e.g. NVDA)
   * @return stock fundamentals data, or null if unavailable
   */
  StockFundamentalsDto getStockFundamentals(String symbol);

  /**
   * Triggers a global refresh of all cached stock fundamental data.
   */
  void refreshAllFundamentals();

}
