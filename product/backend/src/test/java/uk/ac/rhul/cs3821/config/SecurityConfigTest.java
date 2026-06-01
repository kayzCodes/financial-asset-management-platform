package uk.ac.rhul.cs3821.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class SecurityConfigTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private SecurityFilterChain securityFilterChain;

  // Prevent real Firebase filter from interfering in CI
  @MockBean
  private FirebaseTokenFilter firebaseTokenFilter;

  @Test
  void securityFilterChainExists() {
    assertNotNull(securityFilterChain);
  }

  @Test
  void corsPreflightRequest_isAllowed() throws Exception {

    MockMvc mvc = webAppContextSetup(context)
            .apply(springSecurity())
            .build();

    mvc.perform(
                    options("/api/user/registerUser")
                            .header("Origin", "http://localhost:5173")
                            .header("Access-Control-Request-Method", "POST"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Origin"));
  }
}
