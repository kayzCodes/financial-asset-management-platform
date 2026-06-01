package uk.ac.rhul.cs3821.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.rhul.cs3821.dto.NewsDigestDto;
import uk.ac.rhul.cs3821.dto.UserStockDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserNewsDigest;
import uk.ac.rhul.cs3821.marketaux.MarketAuxResponse;
import uk.ac.rhul.cs3821.repository.NewsArticleCacheRepository;
import uk.ac.rhul.cs3821.repository.UserNewsDigestRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.service.MarketAuxService;
import uk.ac.rhul.cs3821.service.UserCryptoService;
import uk.ac.rhul.cs3821.service.UserStockService;

class NewsServiceImplTest {

  @Mock
  private MarketAuxService marketAuxService;
  @Mock
  private UserStockService userStockService;
  @Mock
  private UserCryptoService userCryptoService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private NewsArticleCacheRepository newsArticleCacheRepository;
  @Mock
  private UserNewsDigestRepository userNewsDigestRepository;

  private NewsServiceImpl service;

  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    service = new NewsServiceImpl(
            marketAuxService,
            userStockService,
            userCryptoService,
            userRepository,
            newsArticleCacheRepository,
            userNewsDigestRepository
    );

    // inject rotation size manually
    ReflectionTestUtils.setField(service,
            "rotationChunkSize", 2);

    user = new User();
    user.setId(1L);
    user.setFirebaseUid("uid123");
  }

  // 1 Should return cached digest when not expired
  @Test
  void getPersonalisedNewsDigest_shouldReturnCachedDigest() {

    UserNewsDigest digest = new UserNewsDigest();
    digest.setUser(user);
    digest.setGeneratedAt(LocalDateTime.now());
    digest.setExpiresAt(LocalDateTime.now().plusHours(1));
    digest.setDigestJson("[]");
    digest.setModelVersion("v1");

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));

    when(userNewsDigestRepository
            .findTopByUser_IdAndExpiresAtAfterOrderByGeneratedAtDesc(
                    eq(1L), any()))
            .thenReturn(Optional.of(digest));

    NewsDigestDto result =
            service.getPersonalisedNewsDigest("uid123");

    assertNotNull(result);
    verifyNoInteractions(marketAuxService);
  }

  // 2 Should return empty digest when no symbols
  @Test
  void getPersonalisedNewsDigest_shouldReturnEmpty_whenNoSymbols() {

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));

    when(userNewsDigestRepository
            .findTopByUser_IdAndExpiresAtAfterOrderByGeneratedAtDesc(
                    anyLong(), any()))
            .thenReturn(Optional.empty());

    when(userStockService.getAllUserStocksByFirebaseUid("uid123"))
            .thenReturn(List.of());

    when(userCryptoService.getAllUserCryptoByFirebaseUid("uid123"))
            .thenReturn(List.of());

    NewsDigestDto result =
            service.getPersonalisedNewsDigest("uid123");

    assertNotNull(result);
    assertTrue(result.articles().isEmpty());
    verifyNoInteractions(marketAuxService);
  }

  // 3 Should call API when pool small
  @Test
  void getPersonalisedNewsDigest_shouldCallApi_whenPoolSmall() {

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));

    when(userNewsDigestRepository
            .findTopByUser_IdAndExpiresAtAfterOrderByGeneratedAtDesc(
                    anyLong(), any()))
            .thenReturn(Optional.empty());

    UserStockDto stock = new UserStockDto();
    stock.setTickerSymbol("AAPL");

    when(userStockService.getAllUserStocksByFirebaseUid("uid123"))
            .thenReturn(List.of(stock));

    when(userCryptoService.getAllUserCryptoByFirebaseUid("uid123"))
            .thenReturn(List.of());

    when(newsArticleCacheRepository
            .findBySymbolWithinEntities(any(), any()))
            .thenReturn(List.of());

    when(marketAuxService.fetchNewsBySymbols(any()))
            .thenReturn(new MarketAuxResponse(List.of()));

    service.getPersonalisedNewsDigest("uid123");

    verify(marketAuxService, times(1))
            .fetchNewsBySymbols(anyString());
  }

  // 4 Refresh cooldown blocks repeated refresh
  @Test
  void refreshDigest_shouldBlock_whenWithinCooldown() {

    UserNewsDigest digest = new UserNewsDigest();
    digest.setUser(user);
    digest.setGeneratedAt(LocalDateTime.now());
    digest.setExpiresAt(LocalDateTime.now().plusHours(1));
    digest.setLastRefreshAt(LocalDateTime.now());
    digest.setDigestJson("[]");
    digest.setModelVersion("v1");

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));

    when(userNewsDigestRepository
            .findTopByUser_IdOrderByGeneratedAtDesc(1L))
            .thenReturn(Optional.of(digest));

    NewsDigestDto result =
            service.refreshDigest("uid123");

    assertNotNull(result);
    verify(userNewsDigestRepository, never()).delete(any());
  }
}