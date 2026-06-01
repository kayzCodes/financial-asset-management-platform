package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.dto.AssetAllocationDto;
import uk.ac.rhul.cs3821.dto.ChartPointDto;
import uk.ac.rhul.cs3821.dto.PortfolioOverviewDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.records.HoldingValuation;
import uk.ac.rhul.cs3821.repository.UserCryptoRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.repository.UserStockRepository;
import uk.ac.rhul.cs3821.service.HoldingValuationService;
import uk.ac.rhul.cs3821.service.PortfolioChartService;
import uk.ac.rhul.cs3821.service.PortfolioOverviewService;

/**
 * Implementation of {@link PortfolioOverviewService} providing portfolio
 * summary metrics and allocation details for users.
 */
@Service
@RequiredArgsConstructor
public class PortfolioOverviewServiceImpl
        implements PortfolioOverviewService {

  private static final int SCALE = 6;
  private static final int REFRESH_COOLDOWN_MINUTES = 3;
  private final UserRepository userRepository;
  private final UserStockRepository userStockRepository;
  private final UserCryptoRepository userCryptoRepository;
  private final HoldingValuationService holdingValuationService;
  private final PortfolioChartService portfolioChartService;
  private final Map<String, LocalDateTime> refreshTracker =
          new ConcurrentHashMap<>();

  /**
   * Forces refresh of the cached portfolio overview with cooldown protection.
   *
   * @param firebaseUid the authenticated user identifier
   * @return refreshed {@link PortfolioOverviewDto}
   */
  @CacheEvict(value = "portfolioOverview", key = "#firebaseUid")
  public PortfolioOverviewDto refreshOverview(String firebaseUid) {

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime last = refreshTracker.get(firebaseUid);

    if (last != null
            &&
            last.isAfter(now.minusMinutes(REFRESH_COOLDOWN_MINUTES))) {

      // Cooldown active → return cached version
      return getOverview(firebaseUid);
    }

    refreshTracker.put(firebaseUid, now);

    // Eviction happens because of @CacheEvict
    return getOverview(firebaseUid);
  }

  /**
   * Returns the portfolio overview including valuation, allocation, and chart data.
   *
   * @param firebaseUid the authenticated user identifier
   * @return {@link PortfolioOverviewDto} representing the portfolio summary
   */
  @Cacheable(value = "portfolioOverview", key = "#firebaseUid")
  @Override
  public PortfolioOverviewDto getOverview(String firebaseUid) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<UserStock> stocks =
            userStockRepository.findByUserIdAndIsDeletedFalse(user.getId());

    List<UserCrypto> cryptos =
            userCryptoRepository.findByUserIdAndIsDeletedFalse(user.getId());

    List<HoldingValuation> valuations = new ArrayList<>();

    for (UserStock stock : stocks) {
      HoldingValuation v =
              holdingValuationService.valueStock(user.getId(), stock);

      if (v != null) {
        valuations.add(v);
      }
    }

    for (UserCrypto crypto : cryptos) {
      HoldingValuation v =
              holdingValuationService.valueCrypto(user.getId(), crypto);
      if (v != null) {
        valuations.add(v);
      }
    }


    if (valuations.isEmpty()) {
      return new PortfolioOverviewDto(
              "GBP",
              BigDecimal.ZERO,
              BigDecimal.ZERO,
              BigDecimal.ZERO,
              null,
              BigDecimal.ZERO,
              BigDecimal.ZERO,
              List.of(),
              null,
              null,
              List.of(),
              LocalDateTime.now()
      );
    }

    BigDecimal totalValue = BigDecimal.ZERO;
    BigDecimal totalCost = BigDecimal.ZERO;

    for (HoldingValuation v : valuations) {
      totalValue = totalValue.add(v.currentValueGbp());
      totalCost = totalCost.add(v.costBasisGbp());
    }

    BigDecimal pnl = totalValue.subtract(totalCost);

    BigDecimal pnlPercent = null;
    if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
      pnlPercent = pnl
              .divide(totalCost, SCALE, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
    }

    List<AssetAllocationDto> allocations = new ArrayList<>();

    BigDecimal stocksValue = BigDecimal.ZERO;
    BigDecimal cryptoValue = BigDecimal.ZERO;

    for (HoldingValuation v : valuations) {

      BigDecimal allocationPercent =
              v.currentValueGbp()
                      .divide(totalValue, SCALE, RoundingMode.HALF_UP)
                      .multiply(BigDecimal.valueOf(100));

      allocations.add(new AssetAllocationDto(
              v.holdingId(),
              v.displayName(),
              v.assetType(),
              v.currentValueGbp(),
              allocationPercent,
              v.unrealisedPnlPercent()
      ));

      if ("STOCK".equals(v.assetType())) {
        stocksValue = stocksValue.add(v.currentValueGbp());
      } else {
        cryptoValue = cryptoValue.add(v.currentValueGbp());
      }
    }

    BigDecimal stocksPercent =
            stocksValue.divide(totalValue, SCALE, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

    BigDecimal cryptoPercent =
            cryptoValue.divide(totalValue, SCALE, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

    AssetAllocationDto top =
            allocations.stream()
                    .filter(a -> a.percentChange() != null)
                    .max(Comparator.comparing(AssetAllocationDto::percentChange))
                    .orElse(null);

    AssetAllocationDto worst =
            allocations.stream()
                    .filter(a -> a.percentChange() != null)
                    .min(Comparator.comparing(AssetAllocationDto::percentChange))
                    .orElse(null);

    List<ChartPointDto> chart =
            portfolioChartService.buildPortfolioChart(firebaseUid);

    return new PortfolioOverviewDto(
            "GBP",
            totalValue,
            totalCost,
            pnl,
            pnlPercent,
            stocksPercent,
            cryptoPercent,
            allocations,
            top,
            worst,
            chart,
            LocalDateTime.now()
    );
  }
}