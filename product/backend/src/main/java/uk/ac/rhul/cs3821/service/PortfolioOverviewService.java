package uk.ac.rhul.cs3821.service;

import uk.ac.rhul.cs3821.dto.PortfolioOverviewDto;

/**
 * Service interface for retrieving and refreshing portfolio overview data.
 */
public interface PortfolioOverviewService {

  /**
   * Returns the portfolio overview for the authenticated user.
   *
   * @param firebaseUid the authenticated user identifier
   * @return {@link PortfolioOverviewDto} representing the portfolio summary
   */
  PortfolioOverviewDto getOverview(String firebaseUid);

  /**
   * Forces regeneration of the portfolio overview for the user.
   *
   * @param firebaseUid the authenticated user identifier
   * @return refreshed {@link PortfolioOverviewDto}
   */
  PortfolioOverviewDto refreshOverview(String firebaseUid);
}