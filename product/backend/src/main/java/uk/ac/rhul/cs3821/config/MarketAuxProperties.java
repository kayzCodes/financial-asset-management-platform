package uk.ac.rhul.cs3821.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for MarketAux API integration.
 * Binds external configuration values using the marketaux.api prefix.
 * Holds API key and base URL for outbound requests.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "marketaux.api")
public class MarketAuxProperties {

  private String key;
  private String baseUrl;

}
