package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.alphavantage.CryptoTimeSeriesDailyResponse;
import uk.ac.rhul.cs3821.alphavantage.StockTimeSeriesDailyResponse;
import uk.ac.rhul.cs3821.service.AlphaVantageService;
import uk.ac.rhul.cs3821.service.LivePriceService;

/**
 * Implementation of {@link LivePriceService} retrieving current market prices
 * for stock and crypto assets.
 */
@Service
@RequiredArgsConstructor
public class LivePriceServiceImpl implements LivePriceService {

  private final AlphaVantageService alphaVantageService;

  /**
   * Returns the latest available closing price for a stock ticker.
   *
   * @param ticker stock ticker symbol
   * @return latest stock price or null if unavailable
   */
  @Override
  public BigDecimal getCurrentStockPrice(String ticker) {

    StockTimeSeriesDailyResponse series =
            alphaVantageService.getStockDailySeries(ticker);

    if (series == null || series.getTimeSeriesDaily() == null
            || series.getTimeSeriesDaily().isEmpty()) {
      return null;
    }

    List<String> dates =
            new ArrayList<>(series.getTimeSeriesDaily().keySet());

    dates.sort(Comparator.reverseOrder());

    for (String date : dates) {
      String closeStr =
              series.getTimeSeriesDaily().get(date).getClose();
      if (closeStr == null) {
        continue;
      }
      try {
        return new BigDecimal(closeStr);
      } catch (NumberFormatException ignored) {
        // ignore invalid numeric format from API
      }
    }

    return null;
  }

  /**
   * Returns the latest available closing price for a crypto asset.
   *
   * @param symbol crypto asset symbol
   * @return latest crypto price or null if unavailable
   */
  @Override
  public BigDecimal getCurrentCryptoPrice(String symbol) {

    CryptoTimeSeriesDailyResponse series =
            alphaVantageService.getCryptoDailySeries(symbol);

    if (series == null || series.getTimeSeries() == null
            || series.getTimeSeries().isEmpty()) {
      return null;
    }

    List<String> dates =
            new ArrayList<>(series.getTimeSeries().keySet());

    dates.sort(Comparator.reverseOrder());

    for (String date : dates) {
      String closeStr =
              series.getTimeSeries().get(date).getClose();
      if (closeStr == null) {
        continue;
      }
      try {
        return new BigDecimal(closeStr);
      } catch (NumberFormatException ignored) {
        // ignore invalid numeric format from API
      }
    }

    return null;
  }
}