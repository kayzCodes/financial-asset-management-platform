package uk.ac.rhul.cs3821.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.rhul.cs3821.service.impl.AlphaVantageServiceImpl;

/**
 * Orchestrates automated updates for market data and asset fundamentals.
 * Uses Spring's scheduling capabilities to keep portfolio valuations and
 * stock/crypto metadata synchronized with external providers.
 */
@Component
@RequiredArgsConstructor
public class MarketDataScheduler {

  private final AlphaVantageServiceImpl alphaVantageService;

  /**
   * Refresh cached market data once per weekday.
   * Runs at 06:00 Monday–Friday.
   */
  @Scheduled(cron = "0 0 6 * * MON-FRI")
  public void refreshCachedMarketData() {
    alphaVantageService.refreshCachedData();
  }
}
