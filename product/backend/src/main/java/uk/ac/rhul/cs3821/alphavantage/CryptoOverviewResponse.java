package uk.ac.rhul.cs3821.alphavantage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing the overview details of a cryptocurrency.
 * Mapped from the external provider's response.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoOverviewResponse {

  @JsonProperty("Name")
  private String name;

  @JsonProperty("Symbol")
  private String symbol;

  @JsonProperty("MarketCapitalization")
  private String marketCap;

  @JsonProperty("Description")
  private String description;

  /**
   * Represents the fundamental financial overview data for an asset.
   * Maps market capitalization, valuation ratios, and sector classification.
   */
  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OverviewResponse {

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
  }
}
