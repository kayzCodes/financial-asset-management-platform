package uk.ac.rhul.cs3821.service;

import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.records.HoldingValuation;

/**
 * Service interface for calculating valuation metrics of user asset holdings.
 */
public interface HoldingValuationService {

  /**
   * Calculates valuation metrics for a stock holding.
   *
   * @param userId owning user identifier
   * @param stock  stock holding entity
   * @return calculated {@link HoldingValuation}
   */
  HoldingValuation valueStock(Long userId, UserStock stock);

  /**
   * Calculates valuation metrics for a crypto holding.
   *
   * @param userId owning user identifier
   * @param crypto crypto holding entity
   * @return calculated {@link HoldingValuation}
   */
  HoldingValuation valueCrypto(Long userId, UserCrypto crypto);
}