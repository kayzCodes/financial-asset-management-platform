package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing detailed stock view data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockDetailsDto {

  private String tickerSymbol;
  private String companyName;

  private BigDecimal quantity;
  private BigDecimal averagePurchasePrice;

  // alpha vantage info
  private BigDecimal currentPrice;
  private BigDecimal percentageChange;

  // backend calculated values
  private BigDecimal currentValue;
  private BigDecimal priceDifference;
  private BigDecimal percentageChangeFromAveragePrice;

  private List<ChartPointDto> chartData;

  // flexible for now
  private Map<String, String> keyStatistics;

  private String companyOverview;
}

