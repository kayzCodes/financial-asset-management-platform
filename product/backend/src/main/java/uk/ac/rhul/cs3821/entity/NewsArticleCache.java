package uk.ac.rhul.cs3821.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persists cached news articles fetched from external providers.
 * Enforces uniqueness via provider and URL hash combination.
 * Contains normalized metadata and the original raw JSON payload.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "news_article_cache",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"provider", "url_hash"}
        )
)
public class NewsArticleCache {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String provider;

  @Column(nullable = false)
  private String url;

  @Column(name = "url_hash", nullable = false)
  private String urlHash;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String source;

  @Column(name = "publishedAt", nullable = false)
  private LocalDateTime publishedAt;

  @Column(name = "raw_json", columnDefinition = "TEXT", nullable = false)
  private String rawJson;

  @Column(columnDefinition = "TEXT")
  private String entities;

  @Column
  private String sentiment;

  @Column(name = "fetched_at", nullable = false)
  private LocalDateTime fetchedAt;
}