package uk.ac.rhul.cs3821.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;

class RestConfigurationTest {

  @Test
  void testConfigureRepositoryRestConfiguration_CallsExposeIdsFor() {
    // Arrange
    RestConfiguration restConfig = new RestConfiguration();
    RepositoryRestConfiguration config = Mockito.mock(RepositoryRestConfiguration.class);
    CorsRegistry cors = new CorsRegistry();

    // Act
    restConfig.configureRepositoryRestConfiguration(config, cors);

    // Assert
    verify(config).exposeIdsFor(User.class);
    verify(config).exposeIdsFor(UserStock.class);
    verify(config).exposeIdsFor(UserCrypto.class);

    verifyNoMoreInteractions(config);
  }
}
