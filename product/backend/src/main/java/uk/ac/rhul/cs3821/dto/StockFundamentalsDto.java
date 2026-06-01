package uk.ac.rhul.cs3821.dto;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for stock fundamental data.
 * Contains financial metrics, sector classification, and asset metadata.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockFundamentalsDto {

  private Long id;

  private String symbol;

  private String marketCap;

  private String peRatio;

  private String eps;

  private String sector;

  private String industry;

  private String description;

  private LocalDateTime lastUpdatedAt;
}
