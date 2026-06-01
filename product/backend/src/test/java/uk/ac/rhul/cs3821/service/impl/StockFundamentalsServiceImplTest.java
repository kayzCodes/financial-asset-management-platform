package uk.ac.rhul.cs3821.service.impl;

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
import uk.ac.rhul.cs3821.alphavantage.StockOverviewResponse;
import uk.ac.rhul.cs3821.dto.StockFundamentalsDto;
import uk.ac.rhul.cs3821.entity.StockFundamentals;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.repository.StockFundamentalsRepository;
import uk.ac.rhul.cs3821.repository.UserStockRepository;
import uk.ac.rhul.cs3821.service.AlphaVantageService;

@ExtendWith(MockitoExtension.class)
class StockFundamentalsServiceImplTest {

  @Mock
  private StockFundamentalsRepository stockFundamentalsRepository;

  @Mock
  private AlphaVantageService alphaVantageService;

  @Mock
  private UserStockRepository userStockRepository;

  @InjectMocks
  private StockFundamentalsServiceImpl service;

  @Test
  void getStockFundamentals_returnsExisting_whenNotStale() {
    StockFundamentals entity = new StockFundamentals();
    entity.setSymbol("AAPL");
    entity.setLastUpdatedAt(LocalDateTime.now());

    when(stockFundamentalsRepository.findBySymbol("AAPL"))
            .thenReturn(Optional.of(entity));

    StockFundamentalsDto dto = service.getStockFundamentals("AAPL");

    assertNotNull(dto);
    assertEquals("AAPL", dto.getSymbol());
    verify(alphaVantageService, never()).getStockOverview(any());
  }

  @Test
  void getStockFundamentals_fetchesFromApi_whenMissing() {
    when(stockFundamentalsRepository.findBySymbol("AAPL"))
            .thenReturn(Optional.empty());

    StockOverviewResponse overview = new StockOverviewResponse();
    overview.setMarketCap("1000");
    overview.setPeRatio("10");
    overview.setEps("2.5");

    when(alphaVantageService.getStockOverview("AAPL"))
            .thenReturn(overview);

    when(stockFundamentalsRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    StockFundamentalsDto dto = service.getStockFundamentals("AAPL");

    assertNotNull(dto);
    assertEquals("AAPL", dto.getSymbol());
  }

  @Test
  void getStockFundamentals_returnsExisting_whenApiReturnsNull() {
    StockFundamentals entity = new StockFundamentals();
    entity.setSymbol("AAPL");
    entity.setLastUpdatedAt(LocalDateTime.now().minusDays(2));

    when(stockFundamentalsRepository.findBySymbol("AAPL"))
            .thenReturn(Optional.of(entity));

    when(alphaVantageService.getStockOverview("AAPL"))
            .thenReturn(null);

    StockFundamentalsDto dto = service.getStockFundamentals("AAPL");

    assertNotNull(dto);
    assertEquals("AAPL", dto.getSymbol());
  }

  @Test
  void refreshAllFundamentals_updatesAllUserStockSymbols() {
    UserStock apple = new UserStock();
    apple.setTickerSymbol("AAPL");

    UserStock tesla = new UserStock();
    tesla.setTickerSymbol("TSLA");

    when(userStockRepository.findAll())
            .thenReturn(List.of(apple, tesla));

    StockOverviewResponse overview = new StockOverviewResponse();
    overview.setPeRatio("20");

    when(alphaVantageService.getStockOverview(any()))
            .thenReturn(overview);

    when(stockFundamentalsRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    service.refreshAllFundamentals();

    verify(alphaVantageService, times(2)).getStockOverview(any());
    verify(stockFundamentalsRepository, times(2)).save(any());
  }
}
