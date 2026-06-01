package uk.ac.rhul.cs3821.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Container class for the daily time series data of a cryptocurrency.
 * Wraps the map of date strings to their corresponding OHLC data.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoTimeSeriesDailyResponse {

  @JsonProperty("Time Series (Digital Currency Daily)")
  private Map<String, CryptoDailyBar> timeSeries;
}
