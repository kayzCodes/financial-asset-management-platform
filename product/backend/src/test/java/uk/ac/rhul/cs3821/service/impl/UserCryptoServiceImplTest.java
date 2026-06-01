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
import uk.ac.rhul.cs3821.dto.AddCryptoRequestDto;
import uk.ac.rhul.cs3821.dto.CryptoDetailsDto;
import uk.ac.rhul.cs3821.dto.CryptoFundamentalsDto;
import uk.ac.rhul.cs3821.dto.CryptoTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserCryptoDto;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.entity.AssetTransaction;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.repository.AssetTransactionRepository;
import uk.ac.rhul.cs3821.repository.UserCryptoRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.service.FxRateService;

public class UserCryptoServiceImplTest {

  private final String firebaseUid = "firebase123";
  AddCryptoRequestDto addCryptoRequest;
  CryptoTransactionRequestDto transactionRequest;

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserCryptoRepository userCryptoRepository;
  @InjectMocks
  private UserCryptoServiceImpl userCryptoService;
  @Mock
  private AlphaVantageServiceImpl alphaVantageServiceImpl;
  @Mock
  private CryptoFundamentalsServiceImpl cryptoFundamentalsServiceImpl;
  @Mock
  private FxRateService fxRateService;
  @Mock
  private LivePriceServiceImpl livePriceServiceImpl;
  @Mock
  private AssetTransactionRepository assetTransactionRepository;
  @Mock
  private AlphaVantageDailySeriesProvider alphaVantageDailySeriesProvider;

  private User user;
  private UserCrypto crypto;
  private UserCryptoDto cryptoDto;
  private LocalDateTime now;
  private CryptoTransactionRequestDto initialBuy;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    now = LocalDateTime.now();

    // Mock user
    user = new User();
    user.setId(1L);
    user.setFirebaseUid(firebaseUid);
    user.setFirstName("Alice");
    user.setLastName("Johnson");
    user.setUsername("alicej");
    user.setEmail("alice@example.com");

    // Mock crypto entity
    crypto = new UserCrypto();
    crypto.setHoldingId(10L);
    crypto.setUser(user);
    crypto.setSymbol("BTC");
    crypto.setName("Bitcoin");
    crypto.setQuantity(BigDecimal.valueOf(0.50));
    crypto.setCurrency("USD");
    crypto.setAveragePurchasePrice(BigDecimal.valueOf(30000));
    crypto.setLastTransactionAt(now.minusDays(1));
    crypto.setLastUpdatedPriceAt(now);
    crypto.setNotes("Long-term holding");
    crypto.setCreatedAt(now.minusDays(5));
    crypto.setUpdatedAt(now);

    // DTO version
    cryptoDto = new UserCryptoDto(
            null,
            new UserDto(),
            "BTC",
            "Bitcoin",
            BigDecimal.valueOf(0.50),
            "USD",
            BigDecimal.valueOf(30000),
            now.minusDays(1),
            now,
            false,
            "Long-term holding",
            now.minusDays(5),
            now
    );


    addCryptoRequest = new AddCryptoRequestDto(
            cryptoDto,
            transactionRequest
    );

    initialBuy = new CryptoTransactionRequestDto(
            new BigDecimal("30000"),
            new BigDecimal("0.50"),
            now
    );


  }

  // TEST ADD CRYPTO - SUCCESS
  @Test
  void testAddCrypto_Success() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.save(any(UserCrypto.class)))
            .thenReturn(crypto);

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    UserCryptoDto result =
            userCryptoService.addCrypto(firebaseUid, cryptoDto, initialBuy);

    assertNotNull(result);
    assertEquals("BTC", result.getSymbol());
    assertEquals("Bitcoin", result.getName());
    assertEquals(new BigDecimal("0.5"), result.getQuantity());

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userCryptoRepository).save(any(UserCrypto.class));
    verify(assetTransactionRepository).save(any(AssetTransaction.class));
  }


  // TEST ADD CRYPTO - USER NOT FOUND
  @Test
  void testAddCrypto_UserNotFound() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.addCrypto(firebaseUid, cryptoDto, initialBuy)
    );

    assertEquals(
            "User not found for Firebase UID: " + firebaseUid,
            ex.getMessage()
    );

    verify(userRepository, times(1)).findByFirebaseUid(firebaseUid);
    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }


  @Test
  void testAddCrypto_InitialBuyMissing() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.addCrypto(firebaseUid, cryptoDto, null)
    );

    assertEquals("Initial buy is required", ex.getMessage());
  }

  @Test
  void testAddCrypto_InvalidQuantity() {

    initialBuy.setQuantity(BigDecimal.ZERO);

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.addCrypto(firebaseUid, cryptoDto, initialBuy)
    );

    assertEquals("Buy quantity must be positive", ex.getMessage());
  }


  // TEST GET ALL CRYPTOS - SUCCESS
  @Test
  void testGetAllUserCryptoByFirebaseUid_Success() {
    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findByUserIdAndIsDeletedFalse(1L))
            .thenReturn(List.of(crypto));

    List<UserCryptoDto> result = userCryptoService.getAllUserCryptoByFirebaseUid(firebaseUid);

    assertEquals(1, result.size());
    assertEquals("BTC", result.get(0).getSymbol());

    verify(userRepository, times(1)).findByFirebaseUid(firebaseUid);
    verify(userCryptoRepository, times(1)).findByUserIdAndIsDeletedFalse(1L);
  }

  // TEST GET ALL CRYPTOS - USER NOT FOUND
  @Test
  void testGetAllUserCrypto_UserNotFound() {
    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userCryptoService.getAllUserCryptoByFirebaseUid(firebaseUid));

    assertEquals("User not found for Firebase UID: " + firebaseUid, ex.getMessage());

    verify(userRepository, times(1)).findByFirebaseUid(firebaseUid);
    verify(userCryptoRepository, never()).findByUserIdAndIsDeletedFalse(any());
  }

  @Test
  void getCryptoDetail_returnsCalculatedValuesAndChartData() {

    // ---------- user ----------
    User user = new User();
    user.setId(1L);

    when(userRepository.findByFirebaseUid("uid"))
            .thenReturn(Optional.of(user));

    // ---------- holding ----------
    UserCrypto holding = new UserCrypto();
    holding.setHoldingId(10L);
    holding.setUser(user);
    holding.setSymbol("BTC");
    holding.setName("Bitcoin");
    holding.setQuantity(new BigDecimal("2"));
    holding.setAveragePurchasePrice(new BigDecimal("50000"));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(holding));

    // ---------- fundamentals ----------
    when(cryptoFundamentalsServiceImpl.getCryptoFundamentals("BTC"))
            .thenReturn(new CryptoFundamentalsDto(
                    1L, "BTC", "Bitcoin", null, "desc", null
            ));

    // ---------- market data ----------
    Map<LocalDate, BigDecimal> priceSeries = Map.of(
            LocalDate.now(), new BigDecimal("60000"),
            LocalDate.now().minusDays(1), new BigDecimal("59000")
    );

    when(alphaVantageDailySeriesProvider.getCryptoDailyCloseSeries("BTC"))
            .thenReturn(priceSeries);

    // ---------- act ----------
    CryptoDetailsDto result =
            userCryptoService.getCryptoDetail("uid", 10L);

    // ---------- assertions ----------
    assertNotNull(result);

    assertEquals("BTC", result.getSymbol());
    assertEquals("Bitcoin", result.getName());

    assertEquals(new BigDecimal("60000"), result.getLatestClose());
    assertEquals(new BigDecimal("120000"), result.getCurrentValue());
    assertEquals(new BigDecimal("20000"), result.getPriceDifference());

    assertNull(result.getPercentageChange());

    assertEquals(2, result.getChartData().size());

    // ascending order check
    assertTrue(
            result.getChartData().get(0).date()
                    .compareTo(result.getChartData().get(1).date()) < 0
    );
  }

  // ===============================
  // TEST UPDATE CRYPTO SELL - PARTIAL SELL
  // ===============================
  @Test
  void testUpdateCryptoSell_PartialSell() {

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    new BigDecimal("30000"),
                    new BigDecimal("0.25"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    when(userCryptoRepository.save(any(UserCrypto.class)))
            .thenReturn(crypto);

    UserCryptoDto result =
            userCryptoService.updateCryptoSell(firebaseUid, 10L, request);

    assertNotNull(result);

    assertEquals(new BigDecimal("0.25"), crypto.getQuantity());

    verify(assetTransactionRepository, times(1))
            .save(any(AssetTransaction.class));

    verify(userCryptoRepository, times(1))
            .save(crypto);
  }


  // ===============================
  // TEST UPDATE CRYPTO SELL - FULL SELL
  // ===============================
  @Test
  void testUpdateCryptoSell_FullSellDeletesHolding() {

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    new BigDecimal("30000"),
                    new BigDecimal("0.50"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    when(userCryptoRepository.save(any(UserCrypto.class)))
            .thenReturn(crypto);

    userCryptoService.updateCryptoSell(firebaseUid, 10L, request);

    assertTrue(crypto.getIsDeleted());
    assertEquals(BigDecimal.ZERO, crypto.getQuantity());

    verify(assetTransactionRepository)
            .save(any(AssetTransaction.class));

    verify(userCryptoRepository)
            .save(crypto);
  }


  // ===============================
  // TEST UPDATE CRYPTO SELL - QUANTITY ZERO OR NEGATIVE
  // ===============================
  @Test
  void testUpdateCryptoSell_InvalidQuantity() {

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    new BigDecimal("30000"),
                    BigDecimal.ZERO,
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.updateCryptoSell(firebaseUid, 10L, request)
    );

    assertEquals("Sell quantity must be positive", ex.getMessage());

    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }


  // ===============================
  // TEST UPDATE CRYPTO SELL - SELL MORE THAN OWNED
  // ===============================
  @Test
  void testUpdateCryptoSell_SellMoreThanOwned() {

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    new BigDecimal("30000"),
                    new BigDecimal("1.0"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.updateCryptoSell(firebaseUid, 10L, request)
    );

    assertEquals("Cannot sell more than owned quantity", ex.getMessage());

    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }


  // ===============================
  // TEST UPDATE CRYPTO SELL - NOT OWNER
  // ===============================
  @Test
  void testUpdateCryptoSell_NotOwner() {

    User otherUser = new User();
    otherUser.setId(99L);

    crypto.setUser(otherUser);

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    new BigDecimal("30000"),
                    new BigDecimal("0.1"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.updateCryptoSell(firebaseUid, 10L, request)
    );

    assertEquals("Crypto holding does not belong to user", ex.getMessage());

    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }


  // ===============================
  // TEST UPDATE CRYPTO BUY - SUCCESS
  // ===============================
  @Test
  void testUpdateCryptoBuy_Success() {

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    new BigDecimal("40000"),
                    new BigDecimal("0.50"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    when(userCryptoRepository.save(any(UserCrypto.class)))
            .thenReturn(crypto);

    UserCryptoDto result =
            userCryptoService.updateCryptoBuy(firebaseUid, 10L, request);

    assertNotNull(result);

    // quantity should now be 1.00
    assertEquals(0,
            crypto.getQuantity().compareTo(new BigDecimal("1.00")));

    // average price should be 35000
    assertEquals(0,
            crypto.getAveragePurchasePrice()
                    .compareTo(new BigDecimal("35000")));

    assertNotNull(crypto.getLastTransactionAt());

    verify(assetTransactionRepository)
            .save(any(AssetTransaction.class));

    verify(userCryptoRepository)
            .save(crypto);
  }


  // ===============================
  // TEST UPDATE CRYPTO BUY - USER NOT FOUND
  // ===============================
  @Test
  void testUpdateCryptoBuy_UserNotFound() {

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    new BigDecimal("40000"),
                    new BigDecimal("0.25"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.updateCryptoBuy(firebaseUid, 10L, request)
    );

    assertEquals("User not found", ex.getMessage());

    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }


  // ===============================
  // TEST UPDATE CRYPTO BUY - HOLDING NOT FOUND
  // ===============================
  @Test
  void testUpdateCryptoBuy_HoldingNotFound() {

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    new BigDecimal("40000"),
                    new BigDecimal("0.25"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.updateCryptoBuy(firebaseUid, 10L, request)
    );

    assertEquals("Crypto holding not found", ex.getMessage());

    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }


  // ===============================
  // TEST UPDATE CRYPTO BUY - NOT OWNER
  // ===============================
  @Test
  void testUpdateCryptoBuy_NotOwner() {

    User otherUser = new User();
    otherUser.setId(99L);

    crypto.setUser(otherUser);

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    new BigDecimal("40000"),
                    new BigDecimal("0.25"),
                    now
            );

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.updateCryptoBuy(firebaseUid, 10L, request)
    );

    assertEquals("Crypto holding does not belong to user", ex.getMessage());

    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }


  @Test
  void testDeleteCrypto_Success() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    when(livePriceServiceImpl.getCurrentCryptoPrice("BTC"))
            .thenReturn(new BigDecimal("60000"));

    when(fxRateService.getFxToGbp(any(), any()))
            .thenReturn(BigDecimal.ONE);

    when(userCryptoRepository.save(any(UserCrypto.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    UserCryptoDto result =
            userCryptoService.deleteCrypto(firebaseUid, 10L);

    assertNotNull(result);
    assertEquals("BTC", result.getSymbol());

    assertEquals(0, result.getQuantity().compareTo(BigDecimal.ZERO));
    assertTrue(crypto.getIsDeleted());

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userCryptoRepository).findById(10L);
    verify(assetTransactionRepository).save(any(AssetTransaction.class));
    verify(userCryptoRepository).save(crypto);
  }


  @Test
  void deleteCrypto_throws_whenUserNotFound() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.deleteCrypto(firebaseUid, 10L)
    );

    assertEquals(
            "User not found for Firebase UID: " + firebaseUid,
            ex.getMessage()
    );

    verify(userRepository).findByFirebaseUid(firebaseUid);

    verify(userCryptoRepository, never()).findById(any());
    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }

  @Test
  void deleteCrypto_throws_whenCryptoNotFound() {

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.deleteCrypto(firebaseUid, 10L)
    );

    assertEquals("Crypto not found for ID: 10", ex.getMessage());

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userCryptoRepository).findById(10L);

    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }

  @Test
  void deleteCrypto_throws_whenCryptoDoesNotBelongToUser() {

    User otherUser = new User();
    otherUser.setId(99L);

    crypto.setUser(otherUser);

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.deleteCrypto(firebaseUid, 10L)
    );

    assertEquals(
            "Crypto does not belong to the authenticated user",
            ex.getMessage()
    );

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userCryptoRepository).findById(10L);

    verify(userCryptoRepository, never()).save(any());
    verify(assetTransactionRepository, never()).save(any());
  }

  @Test
  void deleteCrypto_WhenQuantityZero_OnlySoftDeletes() {

    crypto.setQuantity(BigDecimal.ZERO);

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    when(userCryptoRepository.save(any(UserCrypto.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    UserCryptoDto result =
            userCryptoService.deleteCrypto(firebaseUid, 10L);

    assertNotNull(result);
    assertTrue(crypto.getIsDeleted());

    verify(userRepository).findByFirebaseUid(firebaseUid);
    verify(userCryptoRepository).findById(10L);

    verify(assetTransactionRepository, never()).save(any());
    verify(userCryptoRepository).save(crypto);
  }


  @Test
  void getCryptoDetail_throws_whenUserNotFound() {
    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.getCryptoDetail(firebaseUid, 10L)
    );

    assertEquals(
            "User not found for Firebase UID: " + firebaseUid,
            ex.getMessage()
    );
  }

  @Test
  void getCryptoDetail_throws_whenCryptoNotFound() {
    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.getCryptoDetail(firebaseUid, 10L)
    );

    assertEquals(
            "Crypto holding not found for ID: 10",
            ex.getMessage()
    );
  }

  @Test
  void getCryptoDetail_throws_whenCryptoDoesNotBelongToUser() {
    User otherUser = new User();
    otherUser.setId(99L);

    crypto.setUser(otherUser);

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userCryptoRepository.findById(10L))
            .thenReturn(Optional.of(crypto));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userCryptoService.getCryptoDetail(firebaseUid, 10L)
    );

    assertEquals(
            "Crypto holding does not belong to the authenticated user",
            ex.getMessage()
    );
  }

}
