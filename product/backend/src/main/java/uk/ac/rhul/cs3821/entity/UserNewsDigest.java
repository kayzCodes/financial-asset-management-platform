package uk.ac.rhul.cs3821.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a cached AI-generated news digest for a user.
 * Stores the generated summary JSON, expiry metadata, and refresh tracking.
 * Links each digest to a specific user via a mandatory association.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_news_digest")
public class UserNewsDigest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "digest_json", columnDefinition = "TEXT", nullable = false)
  private String digestJson;

  @Column(name = "generated_at", nullable = false)
  private LocalDateTime generatedAt;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "last_refresh_at")
  private LocalDateTime lastRefreshAt;

  @Column(name = "model_version", nullable = false)
  private String modelVersion;

  @Column(name = "last_symbol_index")
  private Integer lastSymbolIndex;
}