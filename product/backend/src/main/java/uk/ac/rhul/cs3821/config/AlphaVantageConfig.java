package uk.ac.rhul.cs3821.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Spring configuration class for the Alpha Vantage API integration.
 * Defines the WebClient bean used to communicate with the external service.
 */
@Configuration
public class AlphaVantageConfig {

  /**
   * Configures and provides the WebClient bean for Alpha Vantage API calls.
   * Sets the base URL and increases the default memory
   * buffer to 5MB to handle large JSON responses.
   *
   * @return the configured WebClient instance.
   */
  @Bean
  public WebClient alphaVantageWebClient() {
    return WebClient.builder()
            .baseUrl("https://www.alphavantage.co")
            .codecs(configurer ->
                    configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)
            )
            .build();
  }
}
