package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.alphavantage.CryptoDailyBar;
import uk.ac.rhul.cs3821.alphavantage.CryptoTimeSeriesDailyResponse;
import uk.ac.rhul.cs3821.alphavantage.StockDailyBar;
import uk.ac.rhul.cs3821.alphavantage.StockTimeSeriesDailyResponse;
import uk.ac.rhul.cs3821.service.AlphaVantageService;

class LivePriceServiceImplTest {

  @Mock
  private AlphaVantageService alphaVantageService;

  private LivePriceServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new LivePriceServiceImpl(alphaVantageService);
  }

  @Test
  void getCurrentStockPrice_returnsLatestPrice() {

    Map<String, StockDailyBar> series = new HashMap<>();

    StockDailyBar bar1 = new StockDailyBar();
    bar1.setClose("100");

    StockDailyBar bar2 = new StockDailyBar();
    bar2.setClose("120");

    series.put("2024-01-01", bar1);
    series.put("2024-01-02", bar2);

    StockTimeSeriesDailyResponse response =
            new StockTimeSeriesDailyResponse();

    response.setTimeSeriesDaily(series);

    when(alphaVantageService.getStockDailySeries("AAPL"))
            .thenReturn(response);

    BigDecimal result = service.getCurrentStockPrice("AAPL");

    assertEquals(new BigDecimal("120"), result);
  }

  @Test
  void getCurrentStockPrice_returnsNullWhenSeriesMissing() {

    when(alphaVantageService.getStockDailySeries("AAPL"))
            .thenReturn(null);

    BigDecimal result = service.getCurrentStockPrice("AAPL");

    assertNull(result);
  }

  @Test
  void getCurrentCryptoPrice_returnsLatestPrice() {

    Map<String, CryptoDailyBar> series = new HashMap<>();

    CryptoDailyBar bar1 = new CryptoDailyBar();
    bar1.setClose("30000");

    CryptoDailyBar bar2 = new CryptoDailyBar();
    bar2.setClose("32000");

    series.put("2024-01-01", bar1);
    series.put("2024-01-02", bar2);

    CryptoTimeSeriesDailyResponse response =
            new CryptoTimeSeriesDailyResponse();

    response.setTimeSeries(series);

    when(alphaVantageService.getCryptoDailySeries("BTC"))
            .thenReturn(response);

    BigDecimal result = service.getCurrentCryptoPrice("BTC");

    assertEquals(new BigDecimal("32000"), result);
  }

  @Test
  void getCurrentCryptoPrice_returnsNullWhenSeriesEmpty() {

    CryptoTimeSeriesDailyResponse response =
            new CryptoTimeSeriesDailyResponse();

    response.setTimeSeries(Map.of());

    when(alphaVantageService.getCryptoDailySeries("BTC"))
            .thenReturn(response);

    BigDecimal result = service.getCurrentCryptoPrice("BTC");

    assertNull(result);
  }

  @Test
  void getCurrentStockPrice_skipsInvalidNumbers() {

    Map<String, StockDailyBar> series = new HashMap<>();

    StockDailyBar invalid = new StockDailyBar();
    invalid.setClose("INVALID");

    StockDailyBar valid = new StockDailyBar();
    valid.setClose("150");

    series.put("2024-01-02", invalid);
    series.put("2024-01-01", valid);

    StockTimeSeriesDailyResponse response =
            new StockTimeSeriesDailyResponse();

    response.setTimeSeriesDaily(series);

    when(alphaVantageService.getStockDailySeries("AAPL"))
            .thenReturn(response);

    BigDecimal result = service.getCurrentStockPrice("AAPL");

    assertEquals(new BigDecimal("150"), result);
  }
}