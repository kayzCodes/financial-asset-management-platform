package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.dto.AddStockRequestDto;
import uk.ac.rhul.cs3821.dto.StockDetailsDto;
import uk.ac.rhul.cs3821.dto.StockFundamentalsDto;
import uk.ac.rhul.cs3821.dto.StockTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserStockDto;
import uk.ac.rhul.cs3821.entity.AssetTransaction;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.repository.AssetTransactionRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.repository.UserStockRepository;
import uk.ac.rhul.cs3821.service.AlphaVantageService;
import uk.ac.rhul.cs3821.service.FxRateService;
import uk.ac.rhul.cs3821.service.StockFundamentalService;

public class UserStockServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStockRepository userStockRepository;

  @Mock
  private AlphaVantageService alphaVantageService;

  @Mock
  private StockFundamentalService stockFundamentalService;

  @Mock
  private FxRateService fxRateService;

  @Mock
  private AssetTransactionRepository assetTransactionRepository;

  @Mock
  private LivePriceServiceImpl livePriceServiceImpl;

  @Mock
  private AlphaVantageDailySeriesProvider alphaVantageDailySeriesProvider;


  @InjectMocks
  private UserStockServiceImpl userStockService;

  private User user;
  private UserStock userStock;
  private UserStockDto userStockDto;
  private LocalDateTime now;
  private String firebaseUid = "firebase123";

  private StockTransactionRequestDto stockInitialBuy;
  private AddStockRequestDto addStockRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    now = LocalDateTime.now();

    user = new User();
    user.setId(1L);
    user.setFirebaseUid(firebaseUid);
    user.setFirstName("Alice");
    user.setLastName("Johnson");
    user.setUsername("alicej");
    user.setEmail("alice@example.com");

    userStock = new UserStock();
    userStock.setHoldingId(10L);
    userStock.setUser(user);
    userStock.setTickerSymbol("AAPL");
    userStock.setCompanyName("Apple Inc.");
    userStock.setQuantity(BigDecimal.valueOf(5));
    userStock.setCurrency("USD");
    userStock.setAveragePurchasePrice(BigDecimal.valueOf(150));
    userStock.setLastTransactionAt(now.minusDays(2));
    userStock.setLastUpdatedPriceAt(now);
    userStock.setNotes("Long term");
    userStock.setCreatedAt(now.minusDays(5));
    userStock.setUpdatedAt(now);

    userStockDto = new UserStockDto(
            null,
            new UserDto(),
            "AAPL",
            "Apple Inc.",
            BigDecimal.valueOf(5),
            "USD",
            BigDecimal.valueOf(150),
            now.minusDays(2),
            now,
            false,
            "Long term",
            now.minusDays(5),
            now
    );

    stockInitialBuy = new StockTransactionRequestDto(
            new BigDecimal("150"),
            new BigDecimal("10"),
            now
    );

    addStockRequest = new AddStockRequestDto(
            userStockDto,
            stockInitialBuy
    );
  }

  // TEST ADD STOCK - SUCCESS
  @Test
  void testAddStock_Success() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.save(any(UserStock.class)))
            .thenReturn(userStock);

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    UserStockDto result =
            userStockService.addStock(firebaseUid, userStockDto, stockInitialBuy);

    assertNotNull(result);
    assertEquals("AAPL", result.getTickerSymbol());

    verify(userRepository, times(1))
            .findByFirebaseUid(firebaseUid);

    verify(userStockRepository, times(1))
            .save(any(UserStock.class));

    verify(assetTransactionRepository, times(1))
            .save(any(AssetTransaction.class));
  }

  // TEST ADD STOCK - USER NOT FOUND
  @Test
  void testAddStock_UserNotFound() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.addStock(firebaseUid, userStockDto, stockInitialBuy)
    );

    assertEquals(
            "User not found for Firebase UID: " + firebaseUid,
            ex.getMessage()
    );

    verify(userRepository, times(1))
            .findByFirebaseUid(firebaseUid);

    verify(userStockRepository, never()).save(any(UserStock.class));
    verify(assetTransactionRepository, never()).save(any());
  }

  // TEST GET ALL STOCKS SUCCESS
  @Test
  void testGetAllUserStocksByFirebaseUid_Success() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findByUserIdAndIsDeletedFalse(1L))
            .thenReturn(List.of(userStock));

    List<UserStockDto> result =
            userStockService.getAllUserStocksByFirebaseUid(firebaseUid);

    assertEquals(1, result.size());
    assertEquals("AAPL", result.get(0).getTickerSymbol());

    verify(userRepository, times(1))
            .findByFirebaseUid(firebaseUid);

    verify(userStockRepository, times(1))
            .findByUserIdAndIsDeletedFalse(1L);
  }

  // TEST GET ALL STOCKS - USER NOT FOUND
  @Test
  void testGetAllUserStocks_UserNotFound() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class, () ->
            userStockService.getAllUserStocksByFirebaseUid(firebaseUid));

    assertEquals("User not found for Firebase UID: " + firebaseUid, ex.getMessage());

    verify(userRepository, times(1)).findByFirebaseUid(firebaseUid);
    verify(userStockRepository, never()).findByUserIdAndIsDeletedFalse(any());
  }

  // ===============================
  // TEST GET STOCK DETAIL - SUCCESS
  // ===============================
  @Test
  void testGetStockDetail_Success() {

    // -------------------------
    // User lookup
    // -------------------------
    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    // -------------------------
    // Fundamentals mock
    // -------------------------
    StockFundamentalsDto fundamentals = new StockFundamentalsDto(
            1L,
            "AAPL",
            "2500000000000",
            "28.5",
            "6.13",
            "Technology",
            "Consumer Electronics",
            "Apple description",
            now
    );

    when(stockFundamentalService.getStockFundamentals("AAPL"))
            .thenReturn(fundamentals);

    // -------------------------
    // Market data mock
    // -------------------------
    Map<LocalDate, BigDecimal> priceSeries = Map.of(
            LocalDate.now(), new BigDecimal("200"),
            LocalDate.now().minusDays(1), new BigDecimal("190")
    );

    when(alphaVantageDailySeriesProvider.getStockDailyCloseSeries("AAPL"))
            .thenReturn(priceSeries);

    // -------------------------
    // Act
    // -------------------------
    StockDetailsDto result =
            userStockService.getStockDetail(firebaseUid, 10L);

    // -------------------------
    // Assert core fields
    // -------------------------
    assertNotNull(result);
    assertEquals("AAPL", result.getTickerSymbol());
    assertEquals("Apple Inc.", result.getCompanyName());

    assertEquals(new BigDecimal("200"), result.getCurrentPrice());
    assertEquals(new BigDecimal("1000"), result.getCurrentValue()); // 200 * 5
    assertEquals(new BigDecimal("250"), result.getPriceDifference()); // (200*5)-(150*5)

    // -------------------------
    // Chart assertions
    // -------------------------
    assertNotNull(result.getChartData());
    assertEquals(2, result.getChartData().size());

    // Chart must be ascending
    assertTrue(
            result.getChartData().get(0).date()
                    .compareTo(result.getChartData().get(1).date()) < 0
    );
  }

  // ===============================
  // TEST GET STOCK DETAIL - USER NOT FOUND
  // ===============================
  @Test
  void testGetStockDetail_UserNotFound() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.getStockDetail(firebaseUid, 10L)
    );

    assertEquals(
            "User not found for Firebase UID: " + firebaseUid,
            ex.getMessage()
    );

    verify(userStockRepository, never()).findById(any());
  }

  // ===============================
  // TEST GET STOCK DETAIL - HOLDING NOT FOUND
  // ===============================
  @Test
  void testGetStockDetail_StockNotFound() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.getStockDetail(firebaseUid, 10L)
    );

    assertEquals("Stock not found for ID: 10", ex.getMessage());
  }

  // ===============================
  // TEST GET STOCK DETAIL - NOT OWNER
  // ===============================
  @Test
  void testGetStockDetail_NotOwner() {

    User otherUser = new User();
    otherUser.setId(99L);

    userStock.setUser(otherUser);

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.getStockDetail(firebaseUid, 10L)
    );

    assertEquals(
            "Stock does not belong to the authenticated user",
            ex.getMessage()
    );
  }

  // ===============================
  // TEST GET STOCK DETAIL - NO MARKET DATA
  // ===============================
  @Test
  void testGetStockDetail_NoMarketData() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    when(stockFundamentalService.getStockFundamentals("AAPL"))
            .thenReturn(null);

    when(alphaVantageDailySeriesProvider.getStockDailyCloseSeries("AAPL"))
            .thenReturn(Map.of());

    StockDetailsDto result =
            userStockService.getStockDetail(firebaseUid, 10L);

    assertNotNull(result);
    assertNull(result.getCurrentPrice());
    assertEquals(0, result.getChartData().size());
  }

  @Test
  void updateStockBuy_success() {

    // Arrange
    StockTransactionRequestDto request =
            new StockTransactionRequestDto(
                    new BigDecimal("200"),
                    new BigDecimal("5"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    when(userStockRepository.save(any(UserStock.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UserStockDto result =
            userStockService.updateStockBuy(firebaseUid, 10L, request);

    // Assert
    assertNotNull(result);

    assertEquals(0,
            result.getQuantity().compareTo(new BigDecimal("10")));

    assertEquals(0,
            result.getAveragePurchasePrice()
                    .compareTo(new BigDecimal("175")));

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userStockRepository).findById(10L);
    verify(assetTransactionRepository).save(any(AssetTransaction.class));
    verify(userStockRepository).save(any(UserStock.class));
  }

  @Test
  void updateStockSell_success_partialSell() {

    // Arrange
    StockTransactionRequestDto request =
            new StockTransactionRequestDto(
                    null,                       // price not used for sell
                    new BigDecimal("2"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    when(userStockRepository.save(any(UserStock.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UserStockDto result =
            userStockService.updateStockSell(firebaseUid, 10L, request);

    // Assert
    assertNotNull(result);

    assertEquals(
            0,
            result.getQuantity().compareTo(new BigDecimal("3"))
    ); // 5 - 2

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userStockRepository).findById(10L);
    verify(assetTransactionRepository).save(any(AssetTransaction.class));
    verify(userStockRepository).save(any(UserStock.class));
  }

  @Test
  void updateStockSell_success_fullSell_marksHoldingDeleted() {

    // Arrange
    StockTransactionRequestDto request =
            new StockTransactionRequestDto(
                    null,
                    new BigDecimal("5"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    when(userStockRepository.save(any(UserStock.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UserStockDto result =
            userStockService.updateStockSell(firebaseUid, 10L, request);

    // Assert
    assertNotNull(result);

    assertEquals(
            0,
            result.getQuantity().compareTo(BigDecimal.ZERO)
    );

    assertTrue(userStock.getIsDeleted());

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userStockRepository).findById(10L);
    verify(assetTransactionRepository).save(any(AssetTransaction.class));
    verify(userStockRepository).save(userStock);
  }

  @Test
  void updateStockSell_throwsWhenQuantityNonPositive() {

    // Arrange
    StockTransactionRequestDto request =
            new StockTransactionRequestDto(
                    null,
                    BigDecimal.ZERO,
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    // Act
    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.updateStockSell(firebaseUid, 10L, request)
    );

    // Assert
    assertEquals("Sell quantity must be positive", ex.getMessage());

    verify(userStockRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }

  @Test
  void updateStockSell_throwsWhenSellingMoreThanOwned() {

    // Arrange
    StockTransactionRequestDto request =
            new StockTransactionRequestDto(
                    null,
                    new BigDecimal("10"), // owned = 5
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    // Act
    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.updateStockSell(firebaseUid, 10L, request)
    );

    // Assert
    assertEquals("Cannot sell more than owned quantity", ex.getMessage());

    verify(userStockRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }

  @Test
  void updateStockSell_throwsWhenNotOwner() {

    User otherUser = new User();
    otherUser.setId(99L);
    userStock.setUser(otherUser);

    StockTransactionRequestDto request =
            new StockTransactionRequestDto(
                    null,
                    BigDecimal.ONE,
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.updateStockSell(firebaseUid, 10L, request)
    );

    assertEquals("Stock does not belong to user", ex.getMessage());

    verify(userStockRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }

  @Test
  void testDeleteStock_Success() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    when(livePriceServiceImpl.getCurrentStockPrice("AAPL"))
            .thenReturn(new BigDecimal("200"));

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    when(userStockRepository.save(any(UserStock.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    UserStockDto result =
            userStockService.deleteStock(firebaseUid, 10L);

    assertNotNull(result);

    assertEquals("AAPL", result.getTickerSymbol());
    assertEquals(0, result.getQuantity().compareTo(BigDecimal.ZERO));

    assertTrue(userStock.getIsDeleted());

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userStockRepository).findById(10L);
    verify(assetTransactionRepository).save(any(AssetTransaction.class));
    verify(userStockRepository).save(userStock);
  }

  @Test
  void deleteStock_throws_whenUserNotFound() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.deleteStock(firebaseUid, 10L)
    );

    assertEquals(
            "User not found for Firebase UID: " + firebaseUid,
            ex.getMessage()
    );

    verify(userStockRepository, never()).findById(any());
    verify(userStockRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }

  @Test
  void deleteStock_throws_whenStockNotFound() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.deleteStock(firebaseUid, 10L)
    );

    assertEquals("Stock not found for ID: 10", ex.getMessage());

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userStockRepository).findById(10L);

    verify(userStockRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }

  @Test
  void deleteStock_throws_whenStockDoesNotBelongToUser() {

    User otherUser = new User();
    otherUser.setId(99L);

    userStock.setUser(otherUser);

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userStockRepository.findById(10L))
            .thenReturn(Optional.of(userStock));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userStockService.deleteStock(firebaseUid, 10L)
    );

    assertEquals(
            "Stock does not belong to the authenticated user",
            ex.getMessage()
    );

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userStockRepository).findById(10L);

    verify(userStockRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }
}
