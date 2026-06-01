package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.rhul.cs3821.alphavantage.CryptoOverviewResponse;
import uk.ac.rhul.cs3821.dto.CryptoFundamentalsDto;
import uk.ac.rhul.cs3821.entity.CryptoFundamentals;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.repository.CryptoFundamentalsRepository;
import uk.ac.rhul.cs3821.repository.UserCryptoRepository;
import uk.ac.rhul.cs3821.service.AlphaVantageService;

@ExtendWith(MockitoExtension.class)
class CryptoFundamentalsServiceImplTest {

  @Mock
  private CryptoFundamentalsRepository cryptoFundamentalsRepository;

  @Mock
  private AlphaVantageService alphaVantageService;

  @Mock
  private UserCryptoRepository userCryptoRepository;

  @InjectMocks
  private CryptoFundamentalsServiceImpl service;

  @Test
  void getCryptoFundamentals_returnsExisting_whenNotStale() {
    CryptoFundamentals entity = new CryptoFundamentals();
    entity.setSymbol("BTC");
    entity.setName("Bitcoin");
    entity.setLastUpdatedAt(LocalDateTime.now());

    when(cryptoFundamentalsRepository.findBySymbol("BTC"))
            .thenReturn(Optional.of(entity));

    CryptoFundamentalsDto dto = service.getCryptoFundamentals("BTC");

    assertNotNull(dto);
    assertEquals("BTC", dto.getSymbol());
    verify(alphaVantageService, never()).getCryptoOverview(any());
  }

  @Test
  void getCryptoFundamentals_fetchesFromApi_whenMissing() {
    when(cryptoFundamentalsRepository.findBySymbol("BTC"))
            .thenReturn(Optional.empty());

    CryptoOverviewResponse overview = new CryptoOverviewResponse();
    overview.setName("Bitcoin");
    overview.setMarketCap("1000000");
    overview.setDescription("BTC description");

    when(alphaVantageService.getCryptoOverview("BTC"))
            .thenReturn(overview);

    when(cryptoFundamentalsRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    CryptoFundamentalsDto dto = service.getCryptoFundamentals("BTC");

    assertNotNull(dto);
    assertEquals("Bitcoin", dto.getName());
    assertEquals(new BigDecimal("1000000"), dto.getMarketCap());
  }

  @Test
  void getCryptoFundamentals_fallsBackToSymbol_whenNameMissing() {
    when(cryptoFundamentalsRepository.findBySymbol("ETH"))
            .thenReturn(Optional.empty());

    CryptoOverviewResponse overview = new CryptoOverviewResponse();
    overview.setName(null);
    overview.setMarketCap("500000");

    when(alphaVantageService.getCryptoOverview("ETH"))
            .thenReturn(overview);

    when(cryptoFundamentalsRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    CryptoFundamentalsDto dto = service.getCryptoFundamentals("ETH");

    assertNotNull(dto);
    assertEquals("ETH", dto.getName());
  }

  @Test
  void getCryptoFundamentals_returnsExisting_whenApiReturnsNull() {
    CryptoFundamentals entity = new CryptoFundamentals();
    entity.setSymbol("BTC");
    entity.setName("Bitcoin");
    entity.setLastUpdatedAt(LocalDateTime.now().minusDays(8));

    when(cryptoFundamentalsRepository.findBySymbol("BTC"))
            .thenReturn(Optional.of(entity));

    when(alphaVantageService.getCryptoOverview("BTC"))
            .thenReturn(null);

    CryptoFundamentalsDto dto = service.getCryptoFundamentals("BTC");

    assertNotNull(dto);
    assertEquals("Bitcoin", dto.getName());
  }

  @Test
  void refreshAllFundamentals_updatesAllUserCryptoSymbols() {
    UserCrypto btc = new UserCrypto();
    btc.setSymbol("BTC");

    UserCrypto eth = new UserCrypto();
    eth.setSymbol("ETH");

    when(userCryptoRepository.findAll())
            .thenReturn(List.of(btc, eth));

    CryptoOverviewResponse overview = new CryptoOverviewResponse();
    overview.setName("Bitcoin");

    when(alphaVantageService.getCryptoOverview(any()))
            .thenReturn(overview);

    when(cryptoFundamentalsRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    service.refreshAllFundamentals();

    verify(alphaVantageService, times(2)).getCryptoOverview(any());
    verify(cryptoFundamentalsRepository, times(2)).save(any());
  }


}
