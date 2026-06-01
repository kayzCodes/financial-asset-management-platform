package uk.ac.rhul.cs3821.service;

import uk.ac.rhul.cs3821.alphavantage.CryptoOverviewResponse;
import uk.ac.rhul.cs3821.alphavantage.CryptoTimeSeriesDailyResponse;
import uk.ac.rhul.cs3821.alphavantage.StockOverviewResponse;
import uk.ac.rhul.cs3821.alphavantage.StockTimeSeriesDailyResponse;

/**
 * Interface for Alpha Vantage market data operations.
 */
public interface AlphaVantageService {

  /**
   * Fetches company fundamentals and financial metrics for a specific stock ticker.
   *
   * @param symbol the stock ticker symbol.
   * @return the fundamental data response, or null if the retrieval fails.
   */
  StockOverviewResponse getStockOverview(String symbol);

  /**
   * Fetches the daily historical price series for a specific stock ticker.
   *
   * @param symbol the stock ticker symbol.
   * @return the daily time-series data, or null if the retrieval fails.
   */
  StockTimeSeriesDailyResponse getStockDailySeries(String symbol);

  /**
   * Fetches fundamental information and metadata for a specific cryptocurrency.
   *
   * @param symbol the cryptocurrency ticker symbol.
   * @return the crypto overview data, or null if the retrieval fails.
   */
  CryptoOverviewResponse getCryptoOverview(String symbol);

  /**
   * Fetches the daily historical price series for a specific cryptocurrency.
   *
   * @param symbol the cryptocurrency ticker symbol.
   * @return the daily time-series data, or null if the retrieval fails.
   */
  CryptoTimeSeriesDailyResponse getCryptoDailySeries(String symbol);

}
