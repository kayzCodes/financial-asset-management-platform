package uk.ac.rhul.cs3821.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * This is the UserStock entity class.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "users_stocks",
        indexes = {@Index(name = "idx_user_stock_active",
                columnList = "user_id", unique = false)
        }
)
public class UserStock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "holding_id")
  private Long holdingId;

  // Many stocks can belong to one user
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "ticker_symbol", nullable = false)
  private String tickerSymbol;

  @Column(name = "company_name")
  private String companyName;

  @Column(name = "quantity", nullable = false)
  private BigDecimal quantity = BigDecimal.ZERO;

  @Column(name = "currency")
  private String currency;

  @Column(name = "average_purchase_price")
  private BigDecimal averagePurchasePrice;

  @Column(name = "last_transaction_at")
  private LocalDateTime lastTransactionAt;

  @Column(name = "last_updated_price_at")
  private LocalDateTime lastUpdatedPriceAt;

  @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
  private Boolean isDeleted = false;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
