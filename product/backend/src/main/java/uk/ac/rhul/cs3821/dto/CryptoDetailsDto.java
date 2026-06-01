package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object providing a comprehensive view of a cryptocurrency holding.
 * Includes user-specific position data, real-time market metrics,
 * historical chart points, and fundamental asset information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoDetailsDto {

  private String symbol;
  private String name;

  private BigDecimal quantity;
  private BigDecimal averagePurchasePrice;

  private BigDecimal latestClose;
  private BigDecimal percentageChange;
  private BigDecimal currentValue;
  private BigDecimal priceDifference;
  private BigDecimal percentageChangeFromAveragePrice;

  private List<ChartPointDto> chartData;

  private CryptoFundamentalsDto fundamentals;
}
