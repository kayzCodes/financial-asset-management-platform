package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.records.HoldingCostResult;
import uk.ac.rhul.cs3821.records.HoldingValuation;
import uk.ac.rhul.cs3821.service.FxRateService;
import uk.ac.rhul.cs3821.service.HoldingValuationService;
import uk.ac.rhul.cs3821.service.LivePriceService;
import uk.ac.rhul.cs3821.service.PortfolioCostService;

/**
 * Implementation of {@link HoldingValuationService} for computing
 * valuation metrics of user stock and crypto holdings.
 */
@Service
@RequiredArgsConstructor
public class HoldingValuationServiceImpl
        implements HoldingValuationService {

  private static final int SCALE = 12;

  private final PortfolioCostService portfolioCostService;
  private final LivePriceService livePriceService;
  private final FxRateService fxRateService;

  /**
   * Calculates valuation metrics for a stock holding using live price and FX rates.
   *
   * @param userId owning user identifier
   * @param stock  stock holding entity
   * @return {@link HoldingValuation} or null if holding or price data is invalid
   */
  @Override
  public HoldingValuation valueStock(Long userId,
                                     UserStock stock) {

    HoldingCostResult cost =
            portfolioCostService.calculateStockCost(userId, stock);

    if (cost.quantityHeld().compareTo(BigDecimal.ZERO) == 0) {
      return null;
    }

    BigDecimal currentPrice =
            livePriceService.getCurrentStockPrice(
                    stock.getTickerSymbol());

    if (currentPrice == null) {
      return null;
    }

    BigDecimal currentFx =
            fxRateService.getCurrentFxToGbp(stock.getCurrency());

    BigDecimal currentValueGbp =
            cost.quantityHeld()
                    .multiply(currentPrice)
                    .multiply(currentFx);

    BigDecimal unrealisedPnlGbp =
            currentValueGbp.subtract(cost.costBasisGbp());

    BigDecimal percent = null;

    if (cost.costBasisGbp().compareTo(BigDecimal.ZERO) > 0) {
      percent = unrealisedPnlGbp
              .divide(cost.costBasisGbp(), SCALE, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
    }

    return new HoldingValuation(
            stock.getHoldingId(),
            stock.getTickerSymbol(),
            "STOCK",
            cost.quantityHeld(),
            cost.costBasisGbp(),
            currentValueGbp,
            unrealisedPnlGbp,
            percent,
            cost.realisedPnlGbp()
    );
  }

  /**
   * Calculates valuation metrics for a crypto holding using live price and FX rates.
   *
   * @param userId owning user identifier
   * @param crypto crypto holding entity
   * @return {@link HoldingValuation} or null if holding or price data is invalid
   */
  @Override
  public HoldingValuation valueCrypto(Long userId,
                                      UserCrypto crypto) {

    HoldingCostResult cost =
            portfolioCostService.calculateCryptoCost(userId, crypto);

    if (cost.quantityHeld().compareTo(BigDecimal.ZERO) == 0) {
      return null;
    }

    BigDecimal currentPrice =
            livePriceService.getCurrentCryptoPrice(
                    crypto.getSymbol());

    if (currentPrice == null) {
      return null;
    }

    BigDecimal currentFx =
            fxRateService.getCurrentFxToGbp(crypto.getCurrency());

    BigDecimal currentValueGbp =
            cost.quantityHeld()
                    .multiply(currentPrice)
                    .multiply(currentFx);

    BigDecimal unrealisedPnlGbp =
            currentValueGbp.subtract(cost.costBasisGbp());

    BigDecimal percent = null;

    if (cost.costBasisGbp().compareTo(BigDecimal.ZERO) > 0) {
      percent = unrealisedPnlGbp
              .divide(cost.costBasisGbp(), SCALE, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
    }

    return new HoldingValuation(
            crypto.getHoldingId(),
            crypto.getSymbol(),
            "CRYPTO",
            cost.quantityHeld(),
            cost.costBasisGbp(),
            currentValueGbp,
            unrealisedPnlGbp,
            percent,
            cost.realisedPnlGbp()
    );
  }
}