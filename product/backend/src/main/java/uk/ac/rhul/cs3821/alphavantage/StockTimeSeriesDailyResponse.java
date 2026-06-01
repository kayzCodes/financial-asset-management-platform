package uk.ac.rhul.cs3821.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Container class for the daily time series data of a stock.
 * Wraps the map of date strings to their corresponding daily bar objects.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockTimeSeriesDailyResponse {

  @JsonProperty("Time Series (Daily)")
  private Map<String, StockDailyBar> timeSeriesDaily;
}
