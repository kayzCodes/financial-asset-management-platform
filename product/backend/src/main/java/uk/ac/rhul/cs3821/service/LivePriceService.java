package uk.ac.rhul.cs3821.service;

import java.math.BigDecimal;

/**
 * Service interface for retrieving live market prices for supported assets.
 */
public interface LivePriceService {

  /**
   * Returns the current market price for a stock ticker.
   *
   * @param ticker stock ticker symbol
   * @return latest stock price
   */
  BigDecimal getCurrentStockPrice(String ticker);

  /**
   * Returns the current market price for a crypto asset.
   *
   * @param symbol crypto asset symbol
   * @return latest crypto price
   */
  BigDecimal getCurrentCryptoPrice(String symbol);
}