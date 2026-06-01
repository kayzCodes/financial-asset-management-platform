package uk.ac.rhul.cs3821.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the daily trading data (Open-High-Low-Close) for a specific stock.
 * Mapped from external API JSON responses.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockDailyBar {

  @JsonProperty("4. close")
  private String close;

  /**
   * Data Transfer Object defining specific attributes of a daily stock bar.
   * Used for nested or specific deserialization contexts.
   */
  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class DailyBarDto {

    @JsonProperty("4. close")
    private String close;
  }
}
