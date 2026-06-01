package uk.ac.rhul.cs3821.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.alphavantage.StockOverviewResponse;
import uk.ac.rhul.cs3821.dto.StockFundamentalsDto;
import uk.ac.rhul.cs3821.entity.StockFundamentals;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.mapper.StockFundamentalsMapper;
import uk.ac.rhul.cs3821.repository.StockFundamentalsRepository;
import uk.ac.rhul.cs3821.repository.UserStockRepository;
import uk.ac.rhul.cs3821.service.AlphaVantageService;
import uk.ac.rhul.cs3821.service.StockFundamentalService;

/**
 * Implementation of the {@link StockFundamentalService} providing
 * cached access and synchronization for stock fundamental data.
 */
@Service
@RequiredArgsConstructor
public class StockFundamentalsServiceImpl implements StockFundamentalService {

  private final StockFundamentalsRepository stockFundamentalsRepository;
  private final AlphaVantageService alphaVantageService;
  private final UserStockRepository userStockRepository;

  /**
   * Retrieves stock fundamental data, returning non-stale database records if available.
   * * Synchronizes with the Alpha Vantage API if data is missing or expired. Updates
   * the local entity with the latest overview metrics, persists the changes,
   * and returns the mapped DTO.
   *
   * @param symbol the stock ticker symbol.
   * @return the stock fundamentals DTO, or null if retrieval fails.
   */
  @Override
  public StockFundamentalsDto getStockFundamentals(String symbol) {

    // 1. Check DB first
    StockFundamentals existing =
            stockFundamentalsRepository.findBySymbol(symbol).orElse(null);

    if (existing != null && !isStale(existing)) {
      return StockFundamentalsMapper.mapToStockFundamentalsDto(existing);
    }

    // 2. Fetch from Alpha Vantage if missing or stale
    StockOverviewResponse overview = alphaVantageService.getStockOverview(symbol);

    if (overview == null) {
      return existing != null
              ? StockFundamentalsMapper.mapToStockFundamentalsDto(existing)
              : null;
    }

    // 3. Create or update entity
    StockFundamentals entity = existing != null ? existing : new StockFundamentals();

    entity.setSymbol(symbol);
    entity.setMarketCap(overview.getMarketCap());
    entity.setPeRatio(overview.getPeRatio());
    entity.setEps(overview.getEps());
    entity.setSector(overview.getSector());
    entity.setIndustry(overview.getIndustry());
    entity.setDescription(overview.getDescription());
    entity.setLastUpdatedAt(LocalDateTime.now());

    // 4. Persist
    StockFundamentals saved = stockFundamentalsRepository.save(entity);

    return StockFundamentalsMapper.mapToStockFundamentalsDto(saved);
  }

  /**
   * Fundamentals are considered stale after 24 hours.
   */
  private boolean isStale(StockFundamentals fundamentals) {
    return fundamentals.getLastUpdatedAt()
            .isBefore(LocalDateTime.now().minus(24, ChronoUnit.HOURS));
  }

  /**
   * Runs once per day at 02:00.
   */
  @Generated
  @Scheduled(cron = "0 0 2 * * *")
  @Override
  public void refreshAllFundamentals() {

    Set<String> symbols = userStockRepository.findAll().stream()
            .map(UserStock::getTickerSymbol)
            .collect(Collectors.toSet());

    for (String symbol : symbols) {

      StockOverviewResponse overview = alphaVantageService.getStockOverview(symbol);

      if (overview == null || overview.getPeRatio() == null) {
        continue; // rate limited or invalid
      }

      StockFundamentals fundamentals =
              stockFundamentalsRepository.findBySymbol(symbol)
                      .orElse(new StockFundamentals());

      fundamentals.setSymbol(symbol);
      fundamentals.setMarketCap(overview.getMarketCap());
      fundamentals.setPeRatio(overview.getPeRatio());
      fundamentals.setEps(overview.getEps());
      fundamentals.setSector(overview.getSector());
      fundamentals.setIndustry(overview.getIndustry());
      fundamentals.setDescription(overview.getDescription());
      fundamentals.setLastUpdatedAt(LocalDateTime.now());

      stockFundamentalsRepository.save(fundamentals);
    }
  }
}
