package uk.ac.rhul.cs3821.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.rhul.cs3821.service.impl.AlphaVantageServiceImpl;

@ExtendWith(MockitoExtension.class)
class MarketDataSchedulerTest {

  @Mock
  private AlphaVantageServiceImpl alphaVantageService;

  @InjectMocks
  private MarketDataScheduler scheduler;

  @Test
  void refreshCachedMarketData_callsAlphaVantageService() {
    // Act
    scheduler.refreshCachedMarketData();

    // Assert
    verify(alphaVantageService, times(1)).refreshCachedData();
  }
}
