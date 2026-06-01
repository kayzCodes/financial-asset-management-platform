package uk.ac.rhul.cs3821.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.rhul.cs3821.repository.NewsArticleCacheRepository;

/**
 * Scheduled component responsible for cleaning stale news articles.
 * Removes cached entries older than the retention window.
 */
@Component
@RequiredArgsConstructor
public class NewsCleanupScheduler {

  private final NewsArticleCacheRepository repository;

  /**
   * Executes daily at 03:30 to delete articles older than seven days.
   */
  @Scheduled(cron = "0 30 3 * * *")
  public void cleanupOldArticles() {

    LocalDateTime cutoff =
            LocalDateTime.now().minusDays(7);

    long deleted = repository.deleteByPublishedAtBefore(cutoff);

    System.out.println("Deleted old news articles: " + deleted);
  }
}