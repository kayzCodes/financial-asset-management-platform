package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing a user's cryptocurrency holding.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCryptoDto {

  private Long holdingId;
  private UserDto user;
  private String symbol;
  private String name;
  private BigDecimal quantity;
  private String currency;
  private BigDecimal averagePurchasePrice;
  private LocalDateTime lastTransactionAt;
  private LocalDateTime lastUpdatedPriceAt;
  private Boolean isDeleted = false;
  private String notes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
