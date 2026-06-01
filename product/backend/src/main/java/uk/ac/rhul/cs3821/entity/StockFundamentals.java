package uk.ac.rhul.cs3821.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persistence entity for stock fundamental data.
 * Stores company metadata, financial ratios, and sector classifications.
 */
@Entity
@Table(
        name = "stock_fundamentals",
        indexes = {@Index(name = "idx_stock_fundamentals_symbol", columnList = "symbol",
                unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
public class StockFundamentals {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(nullable = false, unique = true, length = 20, name = "symbol")
  private String symbol;

  @Column(name = "market_cap")
  private String marketCap;

  @Column(name = "pe_ratio")
  private String peRatio;

  @Column(name = "eps")
  private String eps;

  @Column(name = "sector")
  private String sector;

  @Column(name = "industry")
  private String industry;

  @Column(columnDefinition = "TEXT", name = "description")
  private String description;

  @Column(nullable = false, name = "last_updated_at")
  private LocalDateTime lastUpdatedAt;

  /**
   * Ensures the last_updated_at timestamp is set before the entity is saved.
   */
  @PrePersist
  @PreUpdate
  protected void onUpdate() {
    lastUpdatedAt = LocalDateTime.now();
  }
}
