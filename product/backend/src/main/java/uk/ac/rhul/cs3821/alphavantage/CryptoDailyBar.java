package uk.ac.rhul.cs3821.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the daily Open-High-Low-Close (OHLC) data for a cryptocurrency.
 * Mapped from external API JSON responses.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoDailyBar {

  /**
   * The closing price of the cryptocurrency for the given daily interval.
   */
  @JsonProperty("4. close")
  private String close;
}
