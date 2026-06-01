package uk.ac.rhul.cs3821.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import uk.ac.rhul.cs3821.enums.AssetType;
import uk.ac.rhul.cs3821.enums.TransactionType;

/**
 * Source-of-truth transaction record for all asset activity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "asset_transactions")
public class AssetTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_holding_id")
  private UserStock stockHolding;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "crypto_holding_id")
  private UserCrypto cryptoHolding;

  @Enumerated(EnumType.STRING)
  @Column(name = "asset_type", nullable = false, length = 10)
  private AssetType assetType;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false, length = 10)
  private TransactionType transactionType;

  @Column(name = "quantity", nullable = false, precision = 19, scale = 8)
  private BigDecimal quantity;

  @Column(name = "price_per_unit", nullable = false, precision = 19, scale = 8)
  private BigDecimal pricePerUnit;

  @Column(name = "currency", nullable = false, length = 10)
  private String currency;

  @Column(name = "fx_rate_to_gbp", nullable = false, precision = 19, scale = 12)
  private BigDecimal fxRateToGbp;

  @Column(name = "occurred_at", nullable = false)
  private LocalDateTime occurredAt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}