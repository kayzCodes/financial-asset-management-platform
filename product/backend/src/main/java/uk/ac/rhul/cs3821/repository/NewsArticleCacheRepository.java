package uk.ac.rhul.cs3821.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.rhul.cs3821.entity.NewsArticleCache;

/**
 * Repository for accessing cached news articles.
 * Provides lookup, deduplication, cleanup, and symbol-based queries.
 */
public interface NewsArticleCacheRepository
        extends JpaRepository<NewsArticleCache, Long> {

  Optional<NewsArticleCache> findByProviderAndUrlHash(
          String provider,
          String urlHash
  );

  boolean existsByProviderAndUrlHash(
          String provider,
          String urlHash
  );

  List<NewsArticleCache> findAllByProviderAndUrlHashIn(
          String provider,
          List<String> urlHashes
  );

  long deleteByPublishedAtBefore(
          LocalDateTime cutoff
  );

  @Query("""
              SELECT n FROM NewsArticleCache n
              WHERE n.publishedAt >= :cutoff
              AND n.entities LIKE CONCAT('%,', :symbol, ',%')
              ORDER BY n.publishedAt DESC
          """)
  List<NewsArticleCache> findBySymbolWithinEntities(
          @Param("cutoff") LocalDateTime cutoff,
          @Param("symbol") String symbol
  );
}