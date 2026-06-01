package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.dto.ChartPointDto;
import uk.ac.rhul.cs3821.entity.AssetTransaction;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.enums.TransactionType;
import uk.ac.rhul.cs3821.repository.AssetTransactionRepository;
import uk.ac.rhul.cs3821.repository.UserCryptoRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.repository.UserStockRepository;
import uk.ac.rhul.cs3821.service.FxRateService;
import uk.ac.rhul.cs3821.service.PortfolioChartService;


/**
 * Builds a daily portfolio value chart in GBP (max 252 points, ascending).
 * Uses transaction history to compute quantity held at each date.
 * Uses daily close price series per asset and converts to GBP using FX by date.
 */
@Service
@RequiredArgsConstructor
public class PortfolioChartServiceImpl implements PortfolioChartService {

  private static final int MAX_POINTS = 252;

  private final UserRepository userRepository;
  private final UserStockRepository userStockRepository;
  private final UserCryptoRepository userCryptoRepository;
  private final AssetTransactionRepository assetTransactionRepository;
  private final FxRateService fxRateService;

  private final DailySeriesProvider dailySeriesProvider;

  private BigDecimal getLatestPriceBeforeDate(
          Map<LocalDate, BigDecimal> priceMap,
          LocalDate date
  ) {
    LocalDate current = date;

    while (current.isAfter(LocalDate.of(1999, 1, 1))) {
      BigDecimal price = priceMap.get(current);
      if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
        return price;
      }
      current = current.minusDays(1);
    }

    return null;
  }

  /**
   * Builds the portfolio value time-series chart for the authenticated user.
   *
   * @param firebaseUid the authenticated user identifier
   * @return list of {@link ChartPointDto} representing portfolio value over time
   */
  @Override
  public List<ChartPointDto> buildPortfolioChart(String firebaseUid) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<UserStock> stocks = userStockRepository.findByUserIdAndIsDeletedFalse(user.getId());
    List<UserCrypto> cryptos = userCryptoRepository.findByUserIdAndIsDeletedFalse(user.getId());

    if ((stocks == null || stocks.isEmpty())
            && (cryptos == null || cryptos.isEmpty())) {
      return List.of();
    }

    List<AssetSeries> assetSeries = new ArrayList<>();

    for (UserStock stock : stocks) {
      AssetSeries series = buildStockSeries(user.getId(), stock);
      if (series != null) {
        assetSeries.add(series);
      }
    }

    for (UserCrypto crypto : cryptos) {
      AssetSeries series = buildCryptoSeries(user.getId(), crypto);
      if (series != null) {
        assetSeries.add(series);
      }
    }

    if (assetSeries.isEmpty()) {
      return List.of();
    }

    List<LocalDate> axis = buildUnionAxis(assetSeries);
    if (axis.isEmpty()) {
      return List.of();
    }

    List<ChartPointDto> output = new ArrayList<>();

    for (LocalDate date : axis) {
      BigDecimal totalGbp = BigDecimal.ZERO;

      for (AssetSeries series : assetSeries) {
        BigDecimal qty = series.quantityAt(date);
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
          continue;
        }

        BigDecimal price = getLatestPriceBeforeDate(series.priceByDate, date);
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
          continue;
        }

        BigDecimal fx;
        try {
          fx = fxRateService.getFxToGbp(series.currency, date.atStartOfDay());
        } catch (RuntimeException ex) {
          continue;
        }

        BigDecimal valueGbp = qty.multiply(price).multiply(fx);
        if (valueGbp.compareTo(BigDecimal.ZERO) > 0) {
          totalGbp = totalGbp.add(valueGbp);
        }
      }

      output.add(new ChartPointDto(date.toString(), totalGbp));
    }

    output.sort(Comparator.comparing(p -> LocalDate.parse(p.date())));

    if (output.size() > MAX_POINTS) {
      output = output.subList(output.size() - MAX_POINTS, output.size());
    }

    output.removeIf(p -> p.date() == null
            || p.close() == null
            || p.close().compareTo(BigDecimal.ZERO) <= 0);

    return output;
  }

  /**
   * Builds the price and quantity series for a stock holding.
   *
   * @param userId owning user identifier
   * @param stock  stock holding entity
   * @return constructed AssetSeries or null if data unavailable
   */
  private AssetSeries buildStockSeries(Long userId, UserStock stock) {

    Map<LocalDate, BigDecimal> priceByDate;
    try {
      priceByDate = dailySeriesProvider.getStockDailyCloseSeries(stock.getTickerSymbol());
    } catch (RuntimeException ex) {
      return null;
    }

    if (priceByDate == null || priceByDate.isEmpty()) {
      return null;
    }

    List<AssetTransaction> txs =
            assetTransactionRepository
                    .findByUser_IdAndStockHoldingOrderByOccurredAtAsc(userId, stock);

    NavigableQuantity quantityTimeline = buildQuantityTimeline(txs);

    return new AssetSeries(
            stock.getHoldingId(),
            stock.getTickerSymbol(),
            "STOCK",
            stock.getCurrency(),
            priceByDate,
            quantityTimeline
    );
  }

  /**
   * Builds the price and quantity series for a crypto holding.
   *
   * @param userId owning user identifier
   * @param crypto crypto holding entity
   * @return constructed AssetSeries or null if data unavailable
   */
  private AssetSeries buildCryptoSeries(Long userId, UserCrypto crypto) {

    Map<LocalDate, BigDecimal> priceByDate;
    try {
      priceByDate = dailySeriesProvider.getCryptoDailyCloseSeries(crypto.getSymbol());
    } catch (RuntimeException ex) {
      return null;
    }

    if (priceByDate == null || priceByDate.isEmpty()) {
      return null;
    }

    List<AssetTransaction> txs =
            assetTransactionRepository
                    .findByUser_IdAndCryptoHoldingOrderByOccurredAtAsc(userId, crypto);

    NavigableQuantity quantityTimeline = buildQuantityTimeline(txs);

    return new AssetSeries(
            crypto.getHoldingId(),
            crypto.getSymbol(),
            "CRYPTO",
            crypto.getCurrency(),
            priceByDate,
            quantityTimeline
    );
  }

  /**
   * Builds a unified date axis across all asset price series.
   *
   * @param assets list of asset series
   * @return sorted list of dates limited to chart maximum points
   */
  private List<LocalDate> buildUnionAxis(List<AssetSeries> assets) {

    Set<LocalDate> union = new HashSet<>();
    for (AssetSeries a : assets) {
      union.addAll(a.priceByDate.keySet());
    }

    if (union.isEmpty()) {
      return List.of();
    }

    List<LocalDate> dates = new ArrayList<>(union);
    dates.sort(Comparator.reverseOrder());

    if (dates.size() > MAX_POINTS) {
      dates = dates.subList(0, MAX_POINTS);
    }

    dates.sort(Comparator.naturalOrder());
    return dates;
  }

  /**
   * Builds the end-of-day quantity timeline from asset transactions.
   *
   * @param txs ordered list of asset transactions
   * @return navigable quantity timeline by date
   */
  private NavigableQuantity buildQuantityTimeline(List<AssetTransaction> txs) {

    Map<LocalDate, BigDecimal> endOfDayQty = new HashMap<>();

    BigDecimal qty = BigDecimal.ZERO;

    if (txs != null) {
      for (AssetTransaction tx : txs) {

        if (tx.getOccurredAt() == null || tx.getQuantity() == null) {
          continue;
        }

        LocalDate d = tx.getOccurredAt().toLocalDate();

        if (tx.getTransactionType() == TransactionType.BUY) {
          qty = qty.add(tx.getQuantity());
        } else if (tx.getTransactionType() == TransactionType.SELL) {
          qty = qty.subtract(tx.getQuantity());
        }

        if (qty.compareTo(BigDecimal.ZERO) < 0) {
          qty = BigDecimal.ZERO;
        }

        endOfDayQty.put(d, qty);
      }
    }

    return new NavigableQuantity(endOfDayQty);
  }

  /**
   * Abstraction for fetching daily close series (date -> close) for stocks and crypto.
   * This avoids coupling PortfolioChartService to your "details" use-cases.
   */
  public interface DailySeriesProvider {

    Map<LocalDate, BigDecimal> getStockDailyCloseSeries(String ticker);

    Map<LocalDate, BigDecimal> getCryptoDailyCloseSeries(String symbol);
  }

  private record AssetSeries(
          Long holdingId,
          String symbol,
          String assetType,
          String currency,
          Map<LocalDate, BigDecimal> priceByDate,
          NavigableQuantity quantityTimeline
  ) {

    BigDecimal quantityAt(LocalDate date) {
      return quantityTimeline.quantityAt(date);
    }
  }

  /**
   * Helper class providing navigable quantity lookup by date.
   */
  private static final class NavigableQuantity {

    private final Map<LocalDate, BigDecimal> endOfDayQty;

    private NavigableQuantity(Map<LocalDate, BigDecimal> endOfDayQty) {
      this.endOfDayQty = endOfDayQty;
    }

    /**
     * Returns the quantity held at or before the given date.
     *
     * @param date target date
     * @return quantity held on that date
     */
    BigDecimal quantityAt(LocalDate date) {
      if (endOfDayQty.isEmpty()) {
        return BigDecimal.ZERO;
      }

      LocalDate current = date;

      while (current.isAfter(LocalDate.of(1999, 1, 1))) {
        BigDecimal qty = endOfDayQty.get(current);
        if (qty != null) {
          return qty;
        }
        current = current.minusDays(1);
      }

      return BigDecimal.ZERO;
    }
  }
}