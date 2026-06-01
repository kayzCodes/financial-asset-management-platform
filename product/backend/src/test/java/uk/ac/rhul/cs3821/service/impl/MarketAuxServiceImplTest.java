package uk.ac.rhul.cs3821.service.impl;

import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.rhul.cs3821.config.MarketAuxProperties;
import uk.ac.rhul.cs3821.marketaux.MarketAuxResponse;

class MarketAuxServiceImplTest {

  @Mock
  private MarketAuxProperties properties;

  @Mock
  private WebClient.Builder builder;

  @Mock
  private WebClient webClient;

  @Mock
  private WebClient.RequestHeadersUriSpec uriSpec;

  @Mock
  private WebClient.RequestHeadersSpec headersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  private MarketAuxServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new MarketAuxServiceImpl(properties, builder);
  }

  @Test
  void fetchNewsBySymbols_shouldReturnResponse() {

    when(properties.getBaseUrl()).thenReturn("https://api.test.com");
    when(properties.getKey()).thenReturn("apikey");

    when(builder.baseUrl(anyString())).thenReturn(builder);
    when(builder.build()).thenReturn(webClient);

    when(webClient.get()).thenReturn(uriSpec);

    when(uriSpec.uri(any(java.util.function.Function.class)))
            .thenReturn(headersSpec);

    when(headersSpec.retrieve()).thenReturn(responseSpec);

    MarketAuxResponse expected =
            new MarketAuxResponse(java.util.List.of());

    when(responseSpec.bodyToMono(MarketAuxResponse.class))
            .thenReturn(Mono.just(expected));

    MarketAuxResponse result =
            service.fetchNewsBySymbols("AAPL");

    assertNotNull(result);
    assertEquals(expected, result);

    verify(builder).baseUrl("https://api.test.com");
    verify(webClient).get();
  }

  @Test
  void fetchNewsBySymbols_shouldReturnNull_whenSymbolsBlank() {
    assertNull(service.fetchNewsBySymbols(" "));
    verifyNoInteractions(builder);
  }

  @Test
  void fetchNewsBySymbols_shouldReturnNull_onException() {

    when(properties.getBaseUrl()).thenReturn("https://api.test.com");
    when(properties.getKey()).thenReturn("apikey");

    when(builder.baseUrl(anyString())).thenReturn(builder);
    when(builder.build()).thenReturn(webClient);

    when(webClient.get()).thenThrow(new RuntimeException());

    MarketAuxResponse result =
            service.fetchNewsBySymbols("AAPL");

    assertNull(result);
  }
}