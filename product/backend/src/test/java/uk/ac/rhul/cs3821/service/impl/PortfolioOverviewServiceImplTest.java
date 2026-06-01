package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.dto.PortfolioOverviewDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.records.HoldingValuation;
import uk.ac.rhul.cs3821.repository.UserCryptoRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.repository.UserStockRepository;
import uk.ac.rhul.cs3821.service.HoldingValuationService;
import uk.ac.rhul.cs3821.service.PortfolioChartService;

class PortfolioOverviewServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStockRepository userStockRepository;

  @Mock
  private UserCryptoRepository userCryptoRepository;

  @Mock
  private HoldingValuationService holdingValuationService;

  @Mock
  private PortfolioChartService portfolioChartService;

  @InjectMocks
  private PortfolioOverviewServiceImpl portfolioOverviewService;

  private User user;
  private UserStock stock;
  private UserCrypto crypto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(1L);

    stock = new UserStock();
    stock.setHoldingId(10L);
    stock.setTickerSymbol("AAPL");

    crypto = new UserCrypto();
    crypto.setHoldingId(20L);
    crypto.setSymbol("BTC");
  }

  // ----------------------------------------------------
  // USER NOT FOUND
  // ----------------------------------------------------
  @Test
  void getOverview_userNotFound_throwsException() {

    when(userRepository.findByFirebaseUid("uid"))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> portfolioOverviewService.getOverview("uid")
    );

    assertEquals("User not found", ex.getMessage());
  }

  // ----------------------------------------------------
  // EMPTY PORTFOLIO
  // ----------------------------------------------------
  @Test
  void getOverview_noAssets_returnsEmptyOverview() {

    when(userRepository.findByFirebaseUid("uid"))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findByUserIdAndIsDeletedFalse(1L))
            .thenReturn(List.of());

    when(userCryptoRepository.findByUserIdAndIsDeletedFalse(1L))
            .thenReturn(List.of());

    PortfolioOverviewDto result =
            portfolioOverviewService.getOverview("uid");

    assertNotNull(result);

    assertEquals("GBP", result.baseCurrency());
    assertEquals(BigDecimal.ZERO, result.totalValueGbp());
    assertEquals(BigDecimal.ZERO, result.totalCostGbp());
    assertEquals(BigDecimal.ZERO, result.unrealisedPnlGbp());

    assertTrue(result.assets().isEmpty());
    assertTrue(result.chart().isEmpty());
  }

  // ----------------------------------------------------
  // NORMAL PORTFOLIO
  // ----------------------------------------------------
  @Test
  void getOverview_success_calculatesTotals() {

    when(userRepository.findByFirebaseUid("uid"))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findByUserIdAndIsDeletedFalse(1L))
            .thenReturn(List.of(stock));

    when(userCryptoRepository.findByUserIdAndIsDeletedFalse(1L))
            .thenReturn(List.of(crypto));

    HoldingValuation stockValuation = new HoldingValuation(
            10L,
            "AAPL",
            "STOCK",
            new BigDecimal("5"),
            new BigDecimal("500"),
            new BigDecimal("750"),
            new BigDecimal("250"),
            new BigDecimal("50"),
            BigDecimal.ZERO
    );

    HoldingValuation cryptoValuation = new HoldingValuation(
            20L,
            "BTC",
            "CRYPTO",
            new BigDecimal("1"),
            new BigDecimal("20000"),
            new BigDecimal("25000"),
            new BigDecimal("5000"),
            new BigDecimal("25"),
            BigDecimal.ZERO
    );

    when(holdingValuationService.valueStock(1L, stock))
            .thenReturn(stockValuation);

    when(holdingValuationService.valueCrypto(1L, crypto))
            .thenReturn(cryptoValuation);

    when(portfolioChartService.buildPortfolioChart("uid"))
            .thenReturn(List.of());

    PortfolioOverviewDto result =
            portfolioOverviewService.getOverview("uid");

    assertNotNull(result);

    // Total value = 750 + 25000
    assertEquals(new BigDecimal("25750"), result.totalValueGbp());

    // Total cost = 500 + 20000
    assertEquals(new BigDecimal("20500"), result.totalCostGbp());

    // PnL = 5250
    assertEquals(new BigDecimal("5250"), result.unrealisedPnlGbp());

    assertEquals(2, result.assets().size());

    assertNotNull(result.stocksPercent());
    assertNotNull(result.cryptoPercent());
  }
}