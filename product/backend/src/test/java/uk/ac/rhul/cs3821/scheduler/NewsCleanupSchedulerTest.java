package uk.ac.rhul.cs3821.scheduler;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.repository.NewsArticleCacheRepository;

class NewsCleanupSchedulerTest {

  @Mock
  private NewsArticleCacheRepository repository;

  private NewsCleanupScheduler scheduler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    scheduler = new NewsCleanupScheduler(repository);
  }

  @Test
  void cleanupOldArticles_shouldDeleteArticlesOlderThan7Days() {

    when(repository.deleteByPublishedAtBefore(any()))
            .thenReturn(5L);

    scheduler.cleanupOldArticles();

    ArgumentCaptor<LocalDateTime> captor =
            ArgumentCaptor.forClass(LocalDateTime.class);

    verify(repository, times(1))
            .deleteByPublishedAtBefore(captor.capture());

    LocalDateTime passedCutoff = captor.getValue();
    LocalDateTime expectedCutoff =
            LocalDateTime.now().minusDays(7);

    long secondsDifference =
            Math.abs(passedCutoff.until(expectedCutoff,
                    java.time.temporal.ChronoUnit.SECONDS));

    // Allow small timing difference
    assert (secondsDifference < 2);
  }
}