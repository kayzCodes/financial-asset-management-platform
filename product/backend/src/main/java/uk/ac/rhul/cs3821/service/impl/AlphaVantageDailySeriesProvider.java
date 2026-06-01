package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.ac.rhul.cs3821.alphavantage.CryptoDailyBar;
import uk.ac.rhul.cs3821.alphavantage.CryptoTimeSeriesDailyResponse;
import uk.ac.rhul.cs3821.alphavantage.StockDailyBar;
import uk.ac.rhul.cs3821.alphavantage.StockTimeSeriesDailyResponse;
import uk.ac.rhul.cs3821.service.AlphaVantageService;
import uk.ac.rhul.cs3821.service.impl.PortfolioChartServiceImpl.DailySeriesProvider;

/**
 * Alpha Vantage implementation of {@link DailySeriesProvider}.
 * Retrieves daily close price series for stocks and crypto assets.
 */
@Component
@RequiredArgsConstructor
public class AlphaVantageDailySeriesProvider
        implements DailySeriesProvider {

  private static final int MAX_POINTS = 252;

  private final AlphaVantageService alphaVantageService;

  private final Map<String, Map<LocalDate, BigDecimal>> stockCache = new HashMap<>();
  private final Map<String, Map<LocalDate, BigDecimal>> cryptoCache = new HashMap<>();

  /**
   * Retrieves and sanitizes the daily closing price series for a stock ticker.
   *
   * @param ticker stock ticker symbol
   * @return map of date to closing price
   */
  @Override
  public Map<LocalDate, BigDecimal> getStockDailyCloseSeries(
          String ticker) {

    if (stockCache.containsKey(ticker)) {
      return stockCache.get(ticker);
    }

    StockTimeSeriesDailyResponse response =
            alphaVantageService.getStockDailySeries(ticker);

    if (response == null
            || response.getTimeSeriesDaily() == null
            || response.getTimeSeriesDaily().isEmpty()) {
      return Map.of();
    }

    Map<LocalDate, BigDecimal> cleaned =
            cleanStockSeries(response.getTimeSeriesDaily());

    stockCache.put(ticker, cleaned);

    return cleaned;
  }

  /**
   * Retrieves and sanitizes the daily closing price series for a crypto asset.
   *
   * @param symbol crypto asset symbol
   * @return map of date to closing price
   */
  @Override
  public Map<LocalDate, BigDecimal> getCryptoDailyCloseSeries(
          String symbol) {

    if (cryptoCache.containsKey(symbol)) {
      return cryptoCache.get(symbol);
    }

    CryptoTimeSeriesDailyResponse response =
            alphaVantageService.getCryptoDailySeries(symbol);

    if (response == null
            || response.getTimeSeries() == null
            || response.getTimeSeries().isEmpty()) {
      return Map.of();
    }

    Map<LocalDate, BigDecimal> cleaned =
            cleanCryptoSeries(response.getTimeSeries());

    cryptoCache.put(symbol, cleaned);

    return cleaned;
  }

  /**
   * Cleans and limits raw stock series to valid closing prices.
   * Sorts by date and removes invalid or non-positive values.
   *
   * @param raw raw provider series data
   * @return sanitized map of date to closing price
   */
  private Map<LocalDate, BigDecimal> cleanStockSeries(
          Map<String, StockDailyBar> raw) {

    List<Map.Entry<String, StockDailyBar>> entries =
            new ArrayList<>(raw.entrySet());

    entries.sort((a, b) -> b.getKey().compareTo(a.getKey()));

    int limit = Math.min(MAX_POINTS, entries.size());
    List<Map.Entry<String, StockDailyBar>> limited =
            entries.subList(0, limit);

    limited.sort(Comparator.comparing(e -> LocalDate.parse(e.getKey())));

    Map<LocalDate, BigDecimal> result =
            new LinkedHashMap<>();

    for (Map.Entry<String, StockDailyBar> entry : limited) {

      try {
        LocalDate date = LocalDate.parse(entry.getKey());
        String closeStr = entry.getValue().getClose();

        if (closeStr == null) {
          continue;
        }

        BigDecimal close = new BigDecimal(closeStr);

        if (close.compareTo(BigDecimal.ZERO) > 0) {
          result.put(date, close);
        }

      } catch (Exception ignored) {
        // ignore invalid numeric format from API
      }
    }

    return result;
  }

  /**
   * Cleans and limits raw crypto series to valid closing prices.
   * Sorts by date and removes invalid or non-positive values.
   *
   * @param raw raw provider series data
   * @return sanitized map of date to closing price
   */
  private Map<LocalDate, BigDecimal> cleanCryptoSeries(
          Map<String, CryptoDailyBar> raw) {

    List<Map.Entry<String, CryptoDailyBar>> entries =
            new ArrayList<>(raw.entrySet());

    entries.sort((a, b) -> b.getKey().compareTo(a.getKey()));

    int limit = Math.min(MAX_POINTS, entries.size());
    List<Map.Entry<String, CryptoDailyBar>> limited =
            entries.subList(0, limit);

    limited.sort(Comparator.comparing(e -> LocalDate.parse(e.getKey())));

    Map<LocalDate, BigDecimal> result =
            new LinkedHashMap<>();

    for (Map.Entry<String, CryptoDailyBar> entry : limited) {

      try {
        LocalDate date = LocalDate.parse(entry.getKey());
        String closeStr = entry.getValue().getClose();

        if (closeStr == null) {
          continue;
        }

        BigDecimal close = new BigDecimal(closeStr);

        if (close.compareTo(BigDecimal.ZERO) > 0) {
          result.put(date, close);
        }

      } catch (Exception ignored) {
        // ignore invalid numeric format from API
      }
    }

    return result;
  }
}