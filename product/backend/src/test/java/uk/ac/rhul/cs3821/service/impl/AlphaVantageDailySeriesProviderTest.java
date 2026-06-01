package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

class AlphaVantageDailySeriesProviderTest {

  @Mock
  private AlphaVantageService alphaVantageService;

  private AlphaVantageDailySeriesProvider provider;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    provider = new AlphaVantageDailySeriesProvider(alphaVantageService);
  }

  @Test
  void getStockDailyCloseSeries_returnsCleanSortedSeries() {

    Map<String, StockDailyBar> raw = new HashMap<>();

    StockDailyBar bar1 = new StockDailyBar();
    bar1.setClose("100");

    StockDailyBar bar2 = new StockDailyBar();
    bar2.setClose("110");

    raw.put("2024-01-02", bar1);
    raw.put("2024-01-01", bar2);

    StockTimeSeriesDailyResponse response = new StockTimeSeriesDailyResponse();
    response.setTimeSeriesDaily(raw);

    when(alphaVantageService.getStockDailySeries("AAPL"))
            .thenReturn(response);

    Map<LocalDate, BigDecimal> result =
            provider.getStockDailyCloseSeries("AAPL");

    assertEquals(2, result.size());

    LocalDate first = result.keySet().iterator().next();

    assertEquals(LocalDate.parse("2024-01-01"), first);
    assertEquals(new BigDecimal("110"), result.get(first));
  }

  @Test
  void getCryptoDailyCloseSeries_returnsCleanSortedSeries() {

    Map<String, CryptoDailyBar> raw = new HashMap<>();

    CryptoDailyBar bar1 = new CryptoDailyBar();
    bar1.setClose("50000");

    CryptoDailyBar bar2 = new CryptoDailyBar();
    bar2.setClose("51000");

    raw.put("2024-01-02", bar1);
    raw.put("2024-01-01", bar2);

    CryptoTimeSeriesDailyResponse response = new CryptoTimeSeriesDailyResponse();
    response.setTimeSeries(raw);

    when(alphaVantageService.getCryptoDailySeries("BTC"))
            .thenReturn(response);

    Map<LocalDate, BigDecimal> result =
            provider.getCryptoDailyCloseSeries("BTC");

    assertEquals(2, result.size());

    LocalDate first = result.keySet().iterator().next();

    assertEquals(LocalDate.parse("2024-01-01"), first);
    assertEquals(new BigDecimal("51000"), result.get(first));
  }

  @Test
  void getStockDailyCloseSeries_returnsEmptyWhenApiReturnsNull() {

    when(alphaVantageService.getStockDailySeries("AAPL"))
            .thenReturn(null);

    Map<LocalDate, BigDecimal> result =
            provider.getStockDailyCloseSeries("AAPL");

    assertTrue(result.isEmpty());
  }

  @Test
  void getCryptoDailyCloseSeries_ignoresInvalidCloseValues() {

    Map<String, CryptoDailyBar> raw = new HashMap<>();

    CryptoDailyBar valid = new CryptoDailyBar();
    valid.setClose("40000");

    CryptoDailyBar invalid = new CryptoDailyBar();
    invalid.setClose(null);

    raw.put("2024-01-01", valid);
    raw.put("2024-01-02", invalid);

    CryptoTimeSeriesDailyResponse response = new CryptoTimeSeriesDailyResponse();
    response.setTimeSeries(raw);

    when(alphaVantageService.getCryptoDailySeries("BTC"))
            .thenReturn(response);

    Map<LocalDate, BigDecimal> result =
            provider.getCryptoDailyCloseSeries("BTC");

    assertEquals(1, result.size());
    assertEquals(
            new BigDecimal("40000"),
            result.get(LocalDate.parse("2024-01-01"))
    );
  }
}