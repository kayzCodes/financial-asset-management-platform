package uk.ac.rhul.cs3821.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import uk.ac.rhul.cs3821.dto.NewsDigestDto;
import uk.ac.rhul.cs3821.service.NewsService;

class NewsControllerTest {

  @Mock
  private NewsService newsService;

  @Mock
  private AuthContext authContext;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    NewsController controller =
            new NewsController(newsService, authContext);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setMessageConverters(
                    new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
  }

  @Test
  void getDigest_shouldReturnDigest() throws Exception {

    when(authContext.getFirebaseUid()).thenReturn("uid123");

    NewsDigestDto digest = new NewsDigestDto(
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(6),
            "v1",
            List.of()
    );

    when(newsService.getPersonalisedNewsDigest("uid123"))
            .thenReturn(digest);

    mockMvc.perform(get("/api/news/getDigest"))
            .andExpect(status().isOk());

    verify(newsService)
            .getPersonalisedNewsDigest("uid123");
  }

  @Test
  void refreshNews_shouldReturnRefreshedDigest() throws Exception {

    when(authContext.getFirebaseUid()).thenReturn("uid123");

    NewsDigestDto digest = new NewsDigestDto(
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(6),
            "v1",
            List.of()
    );

    when(newsService.refreshDigest("uid123"))
            .thenReturn(digest);

    mockMvc.perform(post("/api/news/refresh"))
            .andExpect(status().isOk());

    verify(newsService)
            .refreshDigest("uid123");
  }
}