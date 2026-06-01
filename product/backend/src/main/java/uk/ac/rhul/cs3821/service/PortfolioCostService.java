package uk.ac.rhul.cs3821.service;

import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.records.HoldingCostResult;

/**
 * Service interface for calculating cost basis and realised profit or loss for holdings.
 */
public interface PortfolioCostService {

  /**
   * Calculates cost metrics for a stock holding.
   *
   * @param userId owning user identifier
   * @param stock  stock holding entity
   * @return {@link HoldingCostResult} containing cost basis and realised PnL
   */
  HoldingCostResult calculateStockCost(Long userId, UserStock stock);

  /**
   * Calculates cost metrics for a crypto holding.
   *
   * @param userId owning user identifier
   * @param crypto crypto holding entity
   * @return {@link HoldingCostResult} containing cost basis and realised PnL
   */
  HoldingCostResult calculateCryptoCost(Long userId, UserCrypto crypto);
}
