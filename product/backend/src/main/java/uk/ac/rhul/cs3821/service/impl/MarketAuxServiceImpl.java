package uk.ac.rhul.cs3821.service.impl;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.rhul.cs3821.config.MarketAuxProperties;
import uk.ac.rhul.cs3821.marketaux.MarketAuxResponse;
import uk.ac.rhul.cs3821.service.MarketAuxService;

/**
 * Implementation of {@link MarketAuxService} using Spring WebClient.
 * Executes outbound requests to MarketAux API with timeout protection.
 */
@Service
@RequiredArgsConstructor
public class MarketAuxServiceImpl implements MarketAuxService {

  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
  private final MarketAuxProperties properties;
  private final WebClient.Builder webClientBuilder;

  /**
   * Fetches news articles for the given comma-separated symbols.
   * Returns null if symbols are invalid or the request fails.
   *
   * @param symbols comma-separated asset symbols
   * @return {@link MarketAuxResponse} or null on failure
   */
  @Override
  public MarketAuxResponse fetchNewsBySymbols(String symbols) {

    if (symbols == null || symbols.isBlank()) {
      return null;
    }

    WebClient webClient = webClientBuilder
            .baseUrl(properties.getBaseUrl())
            .build();

    try {
      return webClient.get()
              .uri(uriBuilder -> uriBuilder
                      .queryParam("symbols", symbols)
                      .queryParam("filter_entities", "true")
                      .queryParam("language", "en")
                      .queryParam("limit", 20)
                      .queryParam("sort", "published_desc")
                      .queryParam("api_token", properties.getKey())
                      .build())
              .retrieve()
              .bodyToMono(MarketAuxResponse.class)
              .timeout(REQUEST_TIMEOUT)
              .block();

    } catch (Exception ex) {
      // Fail-safe — do not crash digest generation
      return null;
    }
  }
}