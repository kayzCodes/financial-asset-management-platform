package uk.ac.rhul.cs3821.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing the fundamental financial overview of a stock.
 * Includes valuation metrics (PE, EPS) and industry classification.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockOverviewResponse {

  @JsonProperty("MarketCapitalization")
  private String marketCap;

  @JsonProperty("PERatio")
  private String peRatio;

  @JsonProperty("EPS")
  private String eps;

  @JsonProperty("Sector")
  private String sector;

  @JsonProperty("Industry")
  private String industry;

  @JsonProperty("Description")
  private String description;

  /**
   * Container class for the daily time series data of a stock.
   * Wraps the map of date strings to their corresponding daily bar details.
   */
  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TimeSeriesDailyResponse {

    @JsonProperty("Time Series (Daily)")
    private Map<String, StockDailyBar.DailyBarDto> timeSeriesDaily;
  }
}

