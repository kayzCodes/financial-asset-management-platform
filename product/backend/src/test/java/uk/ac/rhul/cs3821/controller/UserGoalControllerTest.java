package uk.ac.rhul.cs3821.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.UserGoalDto;
import uk.ac.rhul.cs3821.service.UserGoalService;

public class UserGoalControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AuthContext authContext;

  @Mock
  private UserGoalService userGoalService;

  @InjectMocks
  private UserGoalController userGoalController;

  private ObjectMapper objectMapper;

  private UserGoalDto goalDto;
  private UserGoalDto createdGoalDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(userGoalController).build();
    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    when(authContext.getFirebaseUid())
            .thenReturn("firebase123");

    goalDto = new UserGoalDto(
            null,
            null,
            "Save for Laptop",
            new BigDecimal(1000.00),
            new BigDecimal(1500.00),
            LocalDateTime.now().plusDays(30),
            "MacBook Pro",
            null
    );

    createdGoalDto = new UserGoalDto(
            1L,
            null,
            "Save for Laptop",
            new BigDecimal(1000.00),
            new BigDecimal(1500.00),
            goalDto.getDeadline(),
            "MacBook Pro",
            LocalDateTime.now()
    );
  }

  // Test POST /api/goals/createGoal
  @Test
  void testCreateGoal_ReturnsCreatedGoal() throws Exception {

    when(userGoalService.createGoalForUser(eq("firebase123"), any(UserGoalDto.class)))
            .thenReturn(createdGoalDto);

    mockMvc.perform(post("/api/userGoals/createGoal")
                    .requestAttr("firebaseUid", "firebase123")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(goalDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.goalTitle").value("Save for Laptop"))
            .andExpect(jsonPath("$.targetAmount").value(1500.00))
            .andExpect(jsonPath("$.description").value("MacBook Pro"));
  }

  // Test GET /api/goals/getGoals (non-empty list)
  @Test
  void testGetUserGoals_ReturnsListOfGoals() throws Exception {

    List<UserGoalDto> list = Arrays.asList(createdGoalDto);

    when(userGoalService.getAllUserGoalsByFirebaseUid("firebase123"))
            .thenReturn(list);

    mockMvc.perform(get("/api/userGoals/getGoals")
                    .requestAttr("firebaseUid", "firebase123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].goalTitle").value("Save for Laptop"));
  }

  // Test GET /api/goals/getGoals (empty list)
  @Test
  void testGetUserGoals_ReturnsEmptyList() throws Exception {

    when(userGoalService.getAllUserGoalsByFirebaseUid("firebase123"))
            .thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/userGoals/getGoals")
                    .requestAttr("firebaseUid", "firebase123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
  }

  // Test PUT /api/userGoals/updateGoal/{goalId}
  @Test
  void testUpdateGoal_ReturnsUpdatedGoal() throws Exception {

    Long goalId = 1L;

    UserGoalDto updatedGoalDto = new UserGoalDto(
            goalId,
            null,
            "Updated Goal Title",
            new BigDecimal(500.00),
            new BigDecimal(2000.00),
            LocalDateTime.now().plusDays(60),
            "Updated description",
            LocalDateTime.now()
    );

    when(userGoalService.updateGoal(
            eq("firebase123"),
            eq(goalId),
            any(UserGoalDto.class)
    )).thenReturn(updatedGoalDto);

    mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .put("/api/userGoals/updateGoal/{goalId}", goalId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(goalDto))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(goalId))
            .andExpect(jsonPath("$.goalTitle").value("Updated Goal Title"))
            .andExpect(jsonPath("$.targetAmount").value(2000.00))
            .andExpect(jsonPath("$.description").value("Updated description"));
  }

  // Test DELETE /api/userGoals/deleteGoal/{goalId}
  @Test
  void testDeleteGoal_ReturnsDeletedGoal() throws Exception {

    Long goalId = 1L;

    UserGoalDto deletedGoalDto = new UserGoalDto(
            goalId,
            null,
            "Save for Laptop",
            new BigDecimal(1000.00),
            new BigDecimal(1500.00),
            LocalDateTime.now().plusDays(30),
            "MacBook Pro",
            LocalDateTime.now()
    );

    when(userGoalService.deleteGoal("firebase123", goalId))
            .thenReturn(deletedGoalDto);

    mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .delete("/api/userGoals/deleteGoal/{goalId}", goalId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(goalId))
            .andExpect(jsonPath("$.goalTitle").value("Save for Laptop"))
            .andExpect(jsonPath("$.targetAmount").value(1500.00))
            .andExpect(jsonPath("$.description").value("MacBook Pro"));
  }

}
