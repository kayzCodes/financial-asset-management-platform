package uk.ac.rhul.cs3821.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persistence entity representing the fundamental data of a cryptocurrency.
 * Stores market metadata and maintains audit timestamps for record creation and updates.
 */
@Entity
@Table(
        name = "crypto_fundamentals",
        uniqueConstraints = {@UniqueConstraint(columnNames = "symbol")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoFundamentals {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "symbol", nullable = false, length = 20)
  private String symbol; // BTC, ETH

  @Column(name = "name", nullable = false)
  private String name; // Bitcoin

  @Column(name = "market_cap", precision = 30, scale = 2)
  private BigDecimal marketCap; // optional

  @Column(name = "description", columnDefinition = "TEXT")
  private String description; // optional

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime lastUpdatedAt;

  /**
   * Initializes audit timestamps before the entity is first persisted.
   */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    lastUpdatedAt = createdAt;
  }

  /**
   * Updates the synchronization timestamp before the entity is modified.
   */
  @PreUpdate
  protected void onUpdate() {
    lastUpdatedAt = LocalDateTime.now();
  }
}
