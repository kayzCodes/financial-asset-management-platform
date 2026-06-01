package uk.ac.rhul.cs3821.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.AddSavingRequestDto;
import uk.ac.rhul.cs3821.dto.UserSavingsDto;
import uk.ac.rhul.cs3821.service.UserSavingsService;

class UserSavingsControllerTest {

  private MockMvc mockMvc;

  @Mock
  private UserSavingsService userSavingsService;

  @Mock
  private AuthContext authContext;

  private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    UserSavingsController controller =
            new UserSavingsController(userSavingsService, authContext);

    mapper = new ObjectMapper();
    mapper.findAndRegisterModules();

    mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
            .build();
  }


  @Test
  void shouldAddSaving() throws Exception {

    AddSavingRequestDto request =
            new AddSavingRequestDto(10L, new BigDecimal("50.00"));

    UserSavingsDto response =
            new UserSavingsDto(1L, 10L, new BigDecimal("50.00"), LocalDateTime.now());

    when(authContext.getFirebaseUid()).thenReturn("uid123");
    when(userSavingsService.addSaving(eq("uid123"), any(AddSavingRequestDto.class)))
            .thenReturn(response);

    mockMvc.perform(post("/api/savings/addSaving")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.goalId").value(10L));
  }


  @Test
  void shouldGetSavingsByGoal() throws Exception {

    UserSavingsDto dto =
            new UserSavingsDto(1L, 10L, new BigDecimal("50.00"), LocalDateTime.now());

    when(authContext.getFirebaseUid()).thenReturn("uid123");
    when(userSavingsService.getSavingsByGoal("uid123", 10L))
            .thenReturn(List.of(dto));

    mockMvc.perform(get("/api/savings/getSavingsByGoal/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].goalId").value(10L));
  }


  @Test
  void shouldGetAllSavings() throws Exception {

    UserSavingsDto dto =
            new UserSavingsDto(1L, 10L, new BigDecimal("50.00"), LocalDateTime.now());

    when(authContext.getFirebaseUid()).thenReturn("uid123");
    when(userSavingsService.getAllSavingsByUser("uid123"))
            .thenReturn(List.of(dto));

    mockMvc.perform(get("/api/savings/getAllSavings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
  }

  @Test
  void shouldDeleteSaving() throws Exception {

    UserSavingsDto dto =
            new UserSavingsDto(1L, 10L, new BigDecimal("50.00"), LocalDateTime.now());

    when(authContext.getFirebaseUid()).thenReturn("uid123");
    when(userSavingsService.deleteSaving("uid123", 1L))
            .thenReturn(dto);

    mockMvc.perform(delete("/api/savings/deleteSavingBySavingId/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
  }
}