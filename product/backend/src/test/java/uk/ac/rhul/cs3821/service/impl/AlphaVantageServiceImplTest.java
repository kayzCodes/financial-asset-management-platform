package uk.ac.rhul.cs3821.service.impl;

import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.rhul.cs3821.alphavantage.StockOverviewResponse;
import uk.ac.rhul.cs3821.alphavantage.StockTimeSeriesDailyResponse;

@ExtendWith(MockitoExtension.class)
class AlphaVantageServiceImplTest {

  /**
   * Utility: create service with mocked WebClient
   */
  private AlphaVantageServiceImpl createService(WebClient webClient) {
    return new AlphaVantageServiceImpl(webClient);
  }

  @Test
  void getStockDailySeries_usesCache_onSecondCall() {
    // -------------------------
    // Arrange
    // -------------------------
    WebClient webClient = mock(WebClient.class);
    AlphaVantageServiceImpl service = new AlphaVantageServiceImpl(webClient);

    WebClient.RequestHeadersUriSpec<?> uriSpec =
            mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec<?> headersSpec =
            mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec =
            mock(WebClient.ResponseSpec.class);

    doReturn(uriSpec).when(webClient).get();


// 👇 THIS is the important fix
    when(uriSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(headersSpec);

    when(headersSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.bodyToMono(String.class))
            .thenReturn(Mono.just("""
                        {
                          "Time Series (Daily)": {
                            "2024-01-01": {
                              "4. close": "100"
                            }
                          }
                        }
                    """));


    // -------------------------
    // Act
    // -------------------------
    StockTimeSeriesDailyResponse first =
            service.getStockDailySeries("AAPL");

    StockTimeSeriesDailyResponse second =
            service.getStockDailySeries("AAPL");

    // -------------------------
    // Assert
    // -------------------------
    assertSame(first, second);
    verify(webClient, times(1)).get();
  }

  @Test
  void getStockOverview_usesCache_onSecondCall() {
    // -------------------------
    // Arrange
    WebClient webClient = mock(WebClient.class);
    AlphaVantageServiceImpl service = createService(webClient);

    WebClient.RequestHeadersUriSpec<?> uriSpec =
            mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec<?> headersSpec =
            mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec =
            mock(WebClient.ResponseSpec.class);

// FIX 1: wildcard generic → use doReturn
    doReturn(uriSpec).when(webClient).get();

// FIX 2: match correct uri(...) overload
    when(uriSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(headersSpec);

    when(headersSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.bodyToMono(String.class))
            .thenReturn(Mono.just("""
                        {
                          "Symbol": "AAPL",
                          "MarketCapitalization": "1000000",
                          "PERatio": "10"
                        }
                    """));

    // -------------------------
    // Act
    // -------------------------
    StockOverviewResponse first =
            service.getStockOverview("AAPL");

    StockOverviewResponse second =
            service.getStockOverview("AAPL");

    // -------------------------
    // Assert
    // -------------------------
    assertSame(first, second);
    verify(webClient, times(1)).get();
  }

  @Test
  void refreshCachedData_doesNotThrow() {
    // -------------------------
    // Arrange
    // -------------------------
    AlphaVantageServiceImpl service =
            new AlphaVantageServiceImpl(
                    mock(WebClient.class)
            );

    // -------------------------
    // Act + Assert
    // -------------------------
    assertDoesNotThrow(service::refreshCachedData);
  }
}
