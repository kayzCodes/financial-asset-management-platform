package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.dto.ChartPointDto;
import uk.ac.rhul.cs3821.entity.AssetTransaction;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.enums.TransactionType;
import uk.ac.rhul.cs3821.repository.AssetTransactionRepository;
import uk.ac.rhul.cs3821.repository.UserCryptoRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.repository.UserStockRepository;
import uk.ac.rhul.cs3821.service.FxRateService;

class PortfolioChartServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStockRepository userStockRepository;

  @Mock
  private UserCryptoRepository userCryptoRepository;

  @Mock
  private AssetTransactionRepository assetTransactionRepository;

  @Mock
  private FxRateService fxRateService;

  @Mock
  private PortfolioChartServiceImpl.DailySeriesProvider dailySeriesProvider;

  @InjectMocks
  private PortfolioChartServiceImpl portfolioChartService;

  private User user;
  private UserStock stock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(1L);

    stock = new UserStock();
    stock.setHoldingId(10L);
    stock.setTickerSymbol("AAPL");
    stock.setCurrency("USD");
    stock.setUser(user);
  }

  @Test
  void buildPortfolioChart_returnsSingleChartPoint() {

    String uid = "uid";

    when(userRepository.findByFirebaseUid(uid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findByUserIdAndIsDeletedFalse(1L))
            .thenReturn(List.of(stock));

    when(userCryptoRepository.findByUserIdAndIsDeletedFalse(1L))
            .thenReturn(List.of());

    // price series
    Map<LocalDate, BigDecimal> priceSeries = Map.of(
            LocalDate.now(), new BigDecimal("200")
    );

    when(dailySeriesProvider.getStockDailyCloseSeries("AAPL"))
            .thenReturn(priceSeries);

    // transaction history (BUY 5)
    AssetTransaction tx = new AssetTransaction();
    tx.setOccurredAt(LocalDateTime.now());
    tx.setQuantity(new BigDecimal("5"));
    tx.setTransactionType(TransactionType.BUY);

    when(assetTransactionRepository
            .findByUser_IdAndStockHoldingOrderByOccurredAtAsc(1L, stock))
            .thenReturn(List.of(tx));

    when(fxRateService.getFxToGbp(anyString(), any()))
            .thenReturn(BigDecimal.ONE);

    // Act
    List<ChartPointDto> result =
            portfolioChartService.buildPortfolioChart(uid);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());

    ChartPointDto point = result.get(0);

    assertEquals(LocalDate.now().toString(), point.date());
    assertEquals(new BigDecimal("1000"), point.close()); // 5 * 200
  }
}