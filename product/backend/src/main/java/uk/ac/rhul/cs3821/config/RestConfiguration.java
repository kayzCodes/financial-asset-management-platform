package uk.ac.rhul.cs3821.config;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;

/**
 * Configures Spring Data REST settings for the application.
 */
public class RestConfiguration implements RepositoryRestConfigurer {

  /**
   * Exposes entity IDs for REST responses.
   *
   * @param config repository REST configuration
   * @param cors   CORS registry
   */
  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
                                                   CorsRegistry cors) {
    config.exposeIdsFor(User.class);
    config.exposeIdsFor(UserStock.class);
    config.exposeIdsFor(UserCrypto.class);
  }
}

