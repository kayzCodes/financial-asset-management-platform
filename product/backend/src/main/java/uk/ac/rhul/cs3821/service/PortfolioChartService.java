package uk.ac.rhul.cs3821.service;

import java.util.List;
import uk.ac.rhul.cs3821.dto.ChartPointDto;

/**
 * Service interface for building portfolio performance chart data.
 */
public interface PortfolioChartService {

  /**
   * Builds the portfolio time-series chart for the authenticated user.
   *
   * @param firebaseUid the authenticated user identifier
   * @return list of {@link ChartPointDto} representing portfolio values over time
   */
  List<ChartPointDto> buildPortfolioChart(String firebaseUid);
}