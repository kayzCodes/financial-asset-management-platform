package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object containing fundamental information for a cryptocurrency.
 * Includes market data, descriptive metadata, and record synchronization timestamps.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoFundamentalsDto {

  private Long id;
  private String symbol;
  private String name;
  private BigDecimal marketCap;
  private String description;
  private LocalDateTime lastUpdatedAt;
}


