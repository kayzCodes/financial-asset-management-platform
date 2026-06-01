package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.entity.AssetTransaction;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.enums.TransactionType;
import uk.ac.rhul.cs3821.records.HoldingCostResult;
import uk.ac.rhul.cs3821.repository.AssetTransactionRepository;
import uk.ac.rhul.cs3821.service.PortfolioCostService;

/**
 * Implementation of {@link PortfolioCostService} for calculating
 * holding cost basis and realised profit or loss.
 */
@Service
@RequiredArgsConstructor
public class PortfolioCostServiceImpl implements PortfolioCostService {

  private static final int SCALE = 12;

  private final AssetTransactionRepository assetTransactionRepository;

  /**
   * Calculates cost metrics for a stock holding using its transactions.
   *
   * @param userId owning user identifier
   * @param stock  stock holding entity
   * @return {@link HoldingCostResult} containing cost basis and realised PnL
   */
  @Override
  public HoldingCostResult calculateStockCost(
          Long userId,
          UserStock stock
  ) {

    List<AssetTransaction> transactions =
            assetTransactionRepository
                    .findByUser_IdAndStockHoldingOrderByOccurredAtAsc(
                            userId,
                            stock
                    );

    return computeCost(transactions);
  }

  /**
   * Calculates cost metrics for a crypto holding using its transactions.
   *
   * @param userId owning user identifier
   * @param crypto crypto holding entity
   * @return {@link HoldingCostResult} containing cost basis and realised PnL
   */
  @Override
  public HoldingCostResult calculateCryptoCost(
          Long userId,
          UserCrypto crypto
  ) {

    List<AssetTransaction> transactions =
            assetTransactionRepository
                    .findByUser_IdAndCryptoHoldingOrderByOccurredAtAsc(
                            userId,
                            crypto
                    );

    return computeCost(transactions);
  }

  /**
   * Computes quantity held, cost basis, and realised profit or loss from transactions.
   *
   * @param transactions ordered list of asset transactions
   * @return {@link HoldingCostResult} with calculated holding metrics
   */
  private HoldingCostResult computeCost(
          List<AssetTransaction> transactions
  ) {


    if (transactions == null || transactions.isEmpty()) {
      return new HoldingCostResult(
              BigDecimal.ZERO,
              BigDecimal.ZERO,
              BigDecimal.ZERO
      );
    }

    BigDecimal qty = BigDecimal.ZERO;
    BigDecimal costGbp = BigDecimal.ZERO;
    BigDecimal realisedGbp = BigDecimal.ZERO;

    for (AssetTransaction tx : transactions) {

      BigDecimal txQty = tx.getQuantity();

      if (tx.getTransactionType() == TransactionType.BUY) {

        BigDecimal buyCost =
                txQty
                        .multiply(tx.getPricePerUnit())
                        .multiply(tx.getFxRateToGbp());

        qty = qty.add(txQty);
        costGbp = costGbp.add(buyCost);

      } else if (tx.getTransactionType() == TransactionType.SELL) {

        if (qty.compareTo(BigDecimal.ZERO) == 0) {
          continue;
        }

        BigDecimal avgCostPerUnit =
                costGbp.divide(qty, SCALE, RoundingMode.HALF_UP);

        BigDecimal sellPriceGbp =
                tx.getPricePerUnit().multiply(tx.getFxRateToGbp());

        BigDecimal realisedPerUnit =
                sellPriceGbp.subtract(avgCostPerUnit);

        BigDecimal realisedTx =
                realisedPerUnit.multiply(txQty);

        realisedGbp = realisedGbp.add(realisedTx);

        BigDecimal reduction =
                txQty.multiply(avgCostPerUnit);

        costGbp = costGbp.subtract(reduction);
        qty = qty.subtract(txQty);

        if (qty.compareTo(BigDecimal.ZERO) == 0) {
          costGbp = BigDecimal.ZERO;
        }
      }
    }

    return new HoldingCostResult(
            qty,
            costGbp,
            realisedGbp
    );
  }
}