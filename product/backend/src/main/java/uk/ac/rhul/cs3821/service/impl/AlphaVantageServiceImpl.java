package uk.ac.rhul.cs3821.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.rhul.cs3821.alphavantage.CryptoOverviewResponse;
import uk.ac.rhul.cs3821.alphavantage.CryptoTimeSeriesDailyResponse;
import uk.ac.rhul.cs3821.alphavantage.StockOverviewResponse;
import uk.ac.rhul.cs3821.alphavantage.StockTimeSeriesDailyResponse;
import uk.ac.rhul.cs3821.service.AlphaVantageService;

/**
 * Implementation of the {@link AlphaVantageService} providing data from the Alpha Vantage API.
 * Handles the communication with external endpoints to retrieve stock and cryptocurrency
 * market data, time series, and fundamental information.
 */
@Service
@RequiredArgsConstructor
public class AlphaVantageServiceImpl implements AlphaVantageService {

  private static final Duration DAILY_TTL = Duration.ofHours(12);
  private static final Duration OVERVIEW_TTL = Duration.ofDays(14);
  private final Map<String, CachedDailySeries> dailyCache = new ConcurrentHashMap<>();
  private final Map<String, CachedOverview> overviewCache = new ConcurrentHashMap<>();
  private final WebClient alphaVantageWebClient;
  private final Map<String, CachedCryptoDailySeries> dailyCryptoCache =
          new ConcurrentHashMap<>();

  @Value("${alphavantage.api-key}")
  private String apiKey;

  /**
   * Retrieves the overview data for a given stock symbol.
   * Checks the local cache first; if missing or stale, fetches from Alpha Vantage
   * and handles rate-limit notifications and parsing.
   *
   * @param symbol the stock ticker symbol.
   * @return the stock overview data, or null if the API limit is reached or the symbol is invalid.
   * @throws RuntimeException if the JSON response cannot be parsed.
   */
  @Override
  public StockOverviewResponse getStockOverview(String symbol) {

    CachedOverview cached = overviewCache.get(symbol);

    if (cached != null) {
      if (isOverviewFresh(cached.fetchedAt)) {
        System.out.println("CACHE HIT (OVERVIEW): " + symbol);
        return cached.data;
      } else {
        // Evict stale entry
        overviewCache.remove(symbol);
        System.out.println("CACHE EVICTED (OVERVIEW): " + symbol);
      }
    }


    System.out.println("CACHE MISS (OVERVIEW): " + symbol);

    String raw = alphaVantageWebClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/query")
                    .queryParam("function", "OVERVIEW")
                    .queryParam("symbol", symbol)
                    .queryParam("apikey", apiKey)
                    .build())
            .retrieve()
            .bodyToMono(String.class)
            .block(Duration.ofSeconds(10));

    if (raw == null
            || raw.contains("\"Information\"")
            || raw.contains("\"Note\"")
            || raw.contains("\"Error Message\"")) {
      return null;
    }

    try {
      StockOverviewResponse parsed =
              new ObjectMapper().readValue(raw, StockOverviewResponse.class);

      overviewCache.put(symbol, new CachedOverview(parsed, Instant.now()));
      return parsed;

    } catch (Exception e) {
      throw new RuntimeException("Failed to parse OVERVIEW", e);
    }
  }

  /**
   * Retrieves the daily time series data for a specific stock symbol.
   * Prioritizes local cache; on a miss, it fetches from Alpha Vantage and
   * validates the response before updating the cache.
   *
   * @param symbol the stock ticker symbol.
   * @return the daily time series response, or null if the API returns an error.
   * @throws RuntimeException if the response cannot be deserialized.
   */
  @Override
  public StockTimeSeriesDailyResponse getStockDailySeries(String symbol) {

    CachedDailySeries cached = dailyCache.get(symbol);

    if (cached != null) {
      if (isDailyFresh(cached.fetchedAt)) {
        System.out.println("CACHE HIT (DAILY): " + symbol);
        return cached.data;
      } else {
        // Evict stale entry
        dailyCache.remove(symbol);
        System.out.println("CACHE EVICTED (DAILY): " + symbol);
      }
    }


    System.out.println("CACHE MISS (DAILY): " + symbol);

    String raw = alphaVantageWebClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/query")
                    .queryParam("function", "TIME_SERIES_DAILY")
                    .queryParam("symbol", symbol)
                    .queryParam("outputsize", "compact")
                    .queryParam("apikey", apiKey)
                    .build())
            .retrieve()
            .bodyToMono(String.class)
            .block(Duration.ofSeconds(10));

    if (raw == null || raw.contains("\"Error Message\"")) {
      return null;
    }

    try {
      StockTimeSeriesDailyResponse parsed =
              new ObjectMapper().readValue(raw, StockTimeSeriesDailyResponse.class);

      if (parsed.getTimeSeriesDaily() != null) {
        dailyCache.put(symbol, new CachedDailySeries(parsed, Instant.now()));
      }

      return parsed;

    } catch (Exception e) {
      throw new RuntimeException("Failed to parse DAILY series", e);
    }
  }

  /**
   * Fetches the overview data for a cryptocurrency asset.
   * Note: Uses Alpha Vantage API; requires careful handling of rate limits and
   * specific digital currency functions.
   *
   * @param symbol the cryptocurrency symbol (e.g., BTC, ETH).
   * @return the parsed cryptocurrency overview response.
   * @throws RuntimeException if the response is invalid or parsing fails.
   */
  @Override
  public CryptoOverviewResponse getCryptoOverview(String symbol) {

    String raw = alphaVantageWebClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/query")
                    .queryParam("function", "OVERVIEW")
                    .queryParam("symbol", symbol)
                    .queryParam("apikey", apiKey)
                    .build())
            .retrieve()
            .bodyToMono(String.class)
            .block(Duration.ofSeconds(10));

    try {
      return new ObjectMapper()
              .readValue(raw, CryptoOverviewResponse.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse CRYPTO OVERVIEW", e);
    }
  }

  /**
   * Retrieves the daily time series for a cryptocurrency in USD.
   * Checks the local cache for fresh data before querying the DIGITAL_CURRENCY_DAILY endpoint.
   *
   * @param symbol the cryptocurrency symbol (e.g., "BTC").
   * @return the daily time series data, or null if the API returns an error or limit note.
   * @throws RuntimeException if the response cannot be mapped to the DTO.
   */
  @Override
  public CryptoTimeSeriesDailyResponse getCryptoDailySeries(String symbol) {

    // ===============================
    // Cache lookup
    // ===============================
    CachedCryptoDailySeries cached = dailyCryptoCache.get(symbol);

    if (cached != null) {
      if (isDailyFresh(cached.fetchedAt)) {
        System.out.println("CACHE HIT (CRYPTO DAILY): " + symbol);
        return cached.data;
      } else {
        // Evict stale entry
        dailyCryptoCache.remove(symbol);
        System.out.println("CACHE EVICTED (CRYPTO DAILY): " + symbol);
      }
    }

    System.out.println("CACHE MISS (CRYPTO DAILY): " + symbol);

    // ===============================
    // Alpha Vantage call
    // ===============================
    String raw = alphaVantageWebClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/query")
                    .queryParam("function", "DIGITAL_CURRENCY_DAILY")
                    .queryParam("symbol", symbol)
                    .queryParam("market", "USD")
                    .queryParam("apikey", apiKey)
                    .build())
            .retrieve()
            .bodyToMono(String.class)
            .block(Duration.ofSeconds(10));

    if (raw == null || raw.contains("\"Error Message\"")) {
      return null;
    }

    try {
      CryptoTimeSeriesDailyResponse parsed =
              new ObjectMapper().readValue(raw, CryptoTimeSeriesDailyResponse.class);

      // ===============================
      // Cache write-back
      // ===============================
      if (parsed.getTimeSeries() != null) {
        dailyCryptoCache.put(
                symbol,
                new CachedCryptoDailySeries(parsed, Instant.now())
        );
      }

      return parsed;

    } catch (Exception e) {
      throw new RuntimeException("Failed to parse CRYPTO DAILY series", e);
    }
  }

  /**
   * Determines if the daily time series data is still valid based on its
   * Time-To-Live (TTL) setting.
   *
   * @param fetchedAt the timestamp when the data was last retrieved from the API.
   * @return true if the current time is within the TTL window, false otherwise.
   */
  private boolean isDailyFresh(Instant fetchedAt) {
    return fetchedAt.plus(DAILY_TTL).isAfter(Instant.now());
  }

  /**
   * Checks if the cached overview metadata is still valid according to the
   * configured OVERVIEW_TTL.
   *
   * @param fetchedAt the instant the data was stored in the cache.
   * @return true if the data is within the validity window, false if it has expired.
   */
  private boolean isOverviewFresh(Instant fetchedAt) {
    return fetchedAt.plus(OVERVIEW_TTL).isAfter(Instant.now());
  }

  /**
   * Iterates through the current cache entries and attempts to synchronize
   * them with the latest market data.
   * * Note: This process is subject to external API rate limits;
   * calling this on a large cache without throttling will result in 429 errors.
   */
  public void refreshCachedData() {

    // Refresh DAILY series
    for (String symbol : dailyCache.keySet()) {
      try {
        getStockDailySeries(symbol);
      } catch (Exception e) {
        System.err.println("Failed to refresh DAILY for " + symbol);
      }
    }

    // Refresh OVERVIEW data
    for (String symbol : overviewCache.keySet()) {
      try {
        getStockOverview(symbol);
      } catch (Exception e) {
        System.err.println("Failed to refresh OVERVIEW for " + symbol);
      }
    }
  }

  /**
   * Internal wrapper to associate a daily crypto time series response with its
   * retrieval timestamp. This metadata is used to determine if the cached
   * data has exceeded its Time-To-Live (TTL).
   */
  private static class CachedCryptoDailySeries {
    private final CryptoTimeSeriesDailyResponse data;
    private final Instant fetchedAt;

    /**
     * Constructs a new cache entry.
     *
     * @param data      the time series response from the API.
     * @param fetchedAt the instant the response was received.
     */
    private CachedCryptoDailySeries(
            CryptoTimeSeriesDailyResponse data,
            Instant fetchedAt) {
      this.data = data;
      this.fetchedAt = fetchedAt;
    }
  }

  /**
   * Internal cache wrapper for daily stock time series data.
   * Pairs the market data response with a retrieval timestamp to enable
   * Time-To-Live (TTL) validation and stale data eviction.
   *
   * @property data The encapsulated daily stock market response.
   * @property fetchedAt The {@link Instant} representing when the data was last synchronized.
   */
  private static class CachedDailySeries {
    private final StockTimeSeriesDailyResponse data;
    private final Instant fetchedAt;

    /**
     * Constructs a new cache entry for a stock daily series.
     *
     * @param data      the time series data response.
     * @param fetchedAt the timestamp of the API retrieval.
     */
    private CachedDailySeries(StockTimeSeriesDailyResponse data, Instant fetchedAt) {
      this.data = data;
      this.fetchedAt = fetchedAt;
    }
  }


  /**
   * Internal cache wrapper for stock overview metadata.
   * Stores the fundamental information of a stock alongside a timestamp
   * to determine the freshness of the data.
   *
   * @property data The response object containing stock fundamental details.
   * @property fetchedAt The {@link Instant} representing when the data was retrieved.
   */
  private static class CachedOverview {
    private final StockOverviewResponse data;
    private final Instant fetchedAt;

    /**
     * Constructs a new cache entry for a stock overview.
     *
     * @param data      The stock overview response.
     * @param fetchedAt The timestamp of the retrieval.
     */
    private CachedOverview(StockOverviewResponse data, Instant fetchedAt) {
      this.data = data;
      this.fetchedAt = fetchedAt;
    }
  }

}
