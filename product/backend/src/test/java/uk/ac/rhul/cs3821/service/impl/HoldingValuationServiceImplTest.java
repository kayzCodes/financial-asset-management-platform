package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.records.HoldingCostResult;
import uk.ac.rhul.cs3821.records.HoldingValuation;
import uk.ac.rhul.cs3821.service.FxRateService;
import uk.ac.rhul.cs3821.service.LivePriceService;
import uk.ac.rhul.cs3821.service.PortfolioCostService;

class HoldingValuationServiceImplTest {

  @Mock
  private PortfolioCostService portfolioCostService;

  @Mock
  private LivePriceService livePriceService;

  @Mock
  private FxRateService fxRateService;

  private HoldingValuationServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new HoldingValuationServiceImpl(
            portfolioCostService,
            livePriceService,
            fxRateService
    );
  }

  @Test
  void valueStock_returnsValuation() {

    UserStock stock = new UserStock();
    stock.setHoldingId(10L);
    stock.setTickerSymbol("AAPL");
    stock.setCurrency("USD");

    HoldingCostResult cost =
            new HoldingCostResult(
                    new BigDecimal("10"),
                    new BigDecimal("1000"),
                    new BigDecimal("200")
            );

    when(portfolioCostService.calculateStockCost(1L, stock))
            .thenReturn(cost);

    when(livePriceService.getCurrentStockPrice("AAPL"))
            .thenReturn(new BigDecimal("150"));

    when(fxRateService.getCurrentFxToGbp("USD"))
            .thenReturn(new BigDecimal("0.8"));

    HoldingValuation result = service.valueStock(1L, stock);

    assertNotNull(result);

    assertEquals("AAPL", result.displayName());
    assertEquals("STOCK", result.assetType());

    BigDecimal expectedValue =
            new BigDecimal("10")
                    .multiply(new BigDecimal("150"))
                    .multiply(new BigDecimal("0.8"));

    assertEquals(0, expectedValue.compareTo(result.currentValueGbp()));

    assertEquals(new BigDecimal("200"), result.realisedPnlGbp());
  }

  @Test
  void valueStock_returnsNullWhenQuantityZero() {

    UserStock stock = new UserStock();
    stock.setTickerSymbol("AAPL");

    HoldingCostResult cost =
            new HoldingCostResult(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );

    when(portfolioCostService.calculateStockCost(1L, stock))
            .thenReturn(cost);

    HoldingValuation result = service.valueStock(1L, stock);

    assertNull(result);
  }

  @Test
  void valueStock_returnsNullWhenPriceUnavailable() {

    UserStock stock = new UserStock();
    stock.setTickerSymbol("AAPL");

    HoldingCostResult cost =
            new HoldingCostResult(
                    new BigDecimal("5"),
                    new BigDecimal("500"),
                    BigDecimal.ZERO
            );

    when(portfolioCostService.calculateStockCost(1L, stock))
            .thenReturn(cost);

    when(livePriceService.getCurrentStockPrice("AAPL"))
            .thenReturn(null);

    HoldingValuation result = service.valueStock(1L, stock);

    assertNull(result);
  }

  @Test
  void valueCrypto_returnsValuation() {

    UserCrypto crypto = new UserCrypto();
    crypto.setHoldingId(20L);
    crypto.setSymbol("BTC");
    crypto.setCurrency("USD");

    HoldingCostResult cost =
            new HoldingCostResult(
                    new BigDecimal("2"),
                    new BigDecimal("40000"),
                    new BigDecimal("5000")
            );

    when(portfolioCostService.calculateCryptoCost(1L, crypto))
            .thenReturn(cost);

    when(livePriceService.getCurrentCryptoPrice("BTC"))
            .thenReturn(new BigDecimal("30000"));

    when(fxRateService.getCurrentFxToGbp("USD"))
            .thenReturn(new BigDecimal("0.8"));

    HoldingValuation result = service.valueCrypto(1L, crypto);

    assertNotNull(result);
    assertEquals("CRYPTO", result.assetType());

    BigDecimal expectedValue =
            new BigDecimal("2")
                    .multiply(new BigDecimal("30000"))
                    .multiply(new BigDecimal("0.8"));

    assertEquals(0, expectedValue.compareTo(result.currentValueGbp()));
  }
}