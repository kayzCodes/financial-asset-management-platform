package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing a crypto transaction request.
 * Contains price, quantity, and optional transaction timestamp.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoTransactionRequestDto {

  private BigDecimal pricePerUnit;
  private BigDecimal quantity;
  private LocalDateTime occurredAt;
}
