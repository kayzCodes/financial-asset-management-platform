package uk.ac.rhul.cs3821.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.PortfolioOverviewDto;
import uk.ac.rhul.cs3821.service.impl.PortfolioOverviewServiceImpl;

class PortfolioControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AuthContext authContext;

  @Mock
  private PortfolioOverviewServiceImpl portfolioOverviewService;

  private PortfolioController controller;

  @BeforeEach
  void setUp() {

    MockitoAnnotations.openMocks(this);

    controller = new PortfolioController(
            authContext,
            portfolioOverviewService
    );

    mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
  }

  @Test
  void getOverview_returnsOverview() throws Exception {

    String uid = "firebase123";

    when(authContext.getFirebaseUid()).thenReturn(uid);
    when(portfolioOverviewService.getOverview(uid))
            .thenReturn(mock(PortfolioOverviewDto.class));

    mockMvc.perform(get("/api/portfolio/overview"))
            .andExpect(status().isOk());

    verify(authContext).getFirebaseUid();
    verify(portfolioOverviewService).getOverview(uid);
  }

  @Test
  void refreshOverview_returnsOverview() throws Exception {

    String uid = "firebase123";

    when(authContext.getFirebaseUid()).thenReturn(uid);
    when(portfolioOverviewService.refreshOverview(uid))
            .thenReturn(mock(PortfolioOverviewDto.class));

    mockMvc.perform(post("/api/portfolio/overview/refresh"))
            .andExpect(status().isOk());

    verify(authContext).getFirebaseUid();
    verify(portfolioOverviewService).refreshOverview(uid);
  }
}