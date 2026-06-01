package uk.ac.rhul.cs3821.service;

import uk.ac.rhul.cs3821.dto.CryptoFundamentalsDto;

/**
 * Service for managing and synchronizing cryptocurrency fundamental data.
 */
public interface CryptoFundamentalsService {

  /**
   * Retrieves cached or fresh fundamental data for a cryptocurrency.
   *
   * @param symbol the cryptocurrency ticker symbol.
   * @return the fundamental data DTO.
   */
  CryptoFundamentalsDto getCryptoFundamentals(String symbol);

  /**
   * Triggers a batch update of all stored fundamental data to ensure global freshness.
   */
  void refreshAllFundamentals();

}
