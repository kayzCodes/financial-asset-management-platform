package uk.ac.rhul.cs3821.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Provider interface for retrieving historical daily closing price series.
 * Supports both stock and crypto assets.
 */
public interface DailySeriesProvider {

  /**
   * Returns daily closing prices for a stock ticker.
   *
   * @param ticker stock ticker symbol
   * @return map of date to closing price
   */
  Map<LocalDate, BigDecimal> getStockDailyCloseSeries(String ticker);

  /**
   * Returns daily closing prices for a crypto symbol.
   *
   * @param symbol crypto asset symbol
   * @return map of date to closing price
   */
  Map<LocalDate, BigDecimal> getCryptoDailyCloseSeries(String symbol);
}