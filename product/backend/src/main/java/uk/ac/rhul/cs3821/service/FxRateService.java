package uk.ac.rhul.cs3821.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service interface for retrieving FX conversion rates to GBP.
 */
public interface FxRateService {

  /**
   * Returns the FX rate to GBP for a currency at a specific date.
   *
   * @param currency source currency code
   * @param atDate   timestamp for the historical rate
   * @return FX rate to GBP
   */
  BigDecimal getFxToGbp(String currency, LocalDateTime atDate);

  /**
   * Returns the current FX rate to GBP for a currency.
   *
   * @param currency source currency code
   * @return latest FX rate to GBP
   */
  BigDecimal getCurrentFxToGbp(String currency);
}