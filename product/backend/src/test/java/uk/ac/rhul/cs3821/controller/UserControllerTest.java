package uk.ac.rhul.cs3821.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserRegistrationDto;
import uk.ac.rhul.cs3821.service.UserService;

public class UserControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AuthContext authContext;

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  private ObjectMapper objectMapper;

  private UserDto userDto;
  private UserRegistrationDto registrationDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

    when(authContext.getFirebaseUid())
            .thenReturn("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1");

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    LocalDateTime now = LocalDateTime.now();

    userDto = new UserDto(
            1L,
            "Hn3iuGlnuxVjcgu6Rtb7WEKSncm1",
            "Alice",
            "Johnson",
            "alicej",
            "alice.johnson@example.com",
            true,
            null,
            null,
            null,
            null,
            null,
            "USD",
            "en",
            "Europe/London",
            "standard",
            "system",
            now.minusDays(1),
            now,
            null
    );

    registrationDto = new UserRegistrationDto();
    registrationDto.setFirebaseUid("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1");
    registrationDto.setFirstName("Alice");
    registrationDto.setLastName("Johnson");
    registrationDto.setUsername("alicej");
    registrationDto.setEmail("alice.johnson@example.com");
  }

  // Test POST /registerUser
  @Test
  void testRegisterUser_ReturnsCreatedUser() throws Exception {

    when(userService.registerUser(any(UserRegistrationDto.class)))
            .thenReturn(userDto);

    mockMvc.perform(post("/api/user/registerUser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registrationDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("alicej"))
            .andExpect(jsonPath("$.email").value("alice.johnson@example.com"))
            .andExpect(jsonPath("$.firstName").value("Alice"));
  }

  //  Test PUT /updateUser
  // Uses a fake firebaseUid in the request attribute
  @Test
  void testUpdateUserProfile_ReturnsUpdatedUser() throws Exception {

    UserDto inputDto = userDto;

    // FIXED: Use same UID as request attribute
    when(userService.updateUserProfile(eq("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1"), any(UserDto.class)))
            .thenReturn(userDto);

    mockMvc.perform(put("/api/user/updateUserProfile")
                    .requestAttr("firebaseUid", "Hn3iuGlnuxVjcgu6Rtb7WEKSncm1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("alicej"))
            .andExpect(jsonPath("$.role").value("standard"));
  }

  //  Test GET /getUser
  @Test
  void testGetUser_ReturnsUser() throws Exception {

    when(userService.getUserByFirebaseUid("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1"))
            .thenReturn(userDto);

    mockMvc.perform(get("/api/user/getUser")
                    .requestAttr("firebaseUid", "Hn3iuGlnuxVjcgu6Rtb7WEKSncm1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("alice.johnson@example.com"))
            .andExpect(jsonPath("$.firstName").value("Alice"));
  }

  // Test GET /checkUserByFirebaseUid/{firebaseUid} — user found
  @Test
  void testCheckUserByFirebaseUid_ReturnsUser() throws Exception {

    when(userService.checkUserByFirebaseUid("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1"))
            .thenReturn(userDto);

    mockMvc.perform(get("/api/user/checkUserByFirebaseUid/Hn3iuGlnuxVjcgu6Rtb7WEKSncm1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("alicej"))
            .andExpect(jsonPath("$.email").value("alice.johnson@example.com"));
  }

  // Test GET /checkUserByFirebaseUid/{firebaseUid} — user not found (returns null)
  @Test
  void testCheckUserByFirebaseUid_ReturnsNull() throws Exception {

    when(userService.checkUserByFirebaseUid("unknownUID"))
            .thenReturn(null);

    mockMvc.perform(get("/api/user/checkUserByFirebaseUid/unknownUID"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  void testUpdateUser_ReturnsUpdatedUser() throws Exception {

    // -------------------------
    // Arrange
    // -------------------------
    UserDto requestDto = new UserDto(
            null,
            null,
            "Alice",
            "Johnson",
            "alicej",
            null,
            true,
            null,
            null,
            null,
            null,
            null,
            "USD",
            "en",
            "Europe/London",
            "standard",
            "system",
            null,
            null,
            null
    );

    UserDto updatedDto = new UserDto(
            1L,
            "Hn3iuGlnuxVjcgu6Rtb7WEKSncm1",
            "Alice",
            "Johnson",
            "alicej",
            "alice.johnson@example.com",
            true,
            null,
            null,
            null,
            null,
            null,
            "USD",
            "en",
            "Europe/London",
            "standard",
            "system",
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now(),
            null
    );

    when(userService.updateUser(eq("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1"), any(UserDto.class)))
            .thenReturn(updatedDto);

    // -------------------------
    // Act + Assert
    // -------------------------
    mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .put("/api/user/updateUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.firstName").value("Alice"))
            .andExpect(jsonPath("$.lastName").value("Johnson"))
            .andExpect(jsonPath("$.username").value("alicej"))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.preferredLanguage").value("en"))
            .andExpect(jsonPath("$.timezone").value("Europe/London"));
  }

  @Test
  void testUpdateUserPreferences_ReturnsUpdatedPreferences() throws Exception {

    // -------------------------
    // Arrange
    // -------------------------
    UserDto requestDto = new UserDto(
            null,
            null,
            null,
            null,
            null,
            null,
            true,
            null,
            null,
            null,
            null,
            null,
            "GBP",
            "en",
            "Europe/London",
            "dark",
            null,
            null,
            null,
            null
    );

    UserDto updatedDto = new UserDto(
            1L,
            "Hn3iuGlnuxVjcgu6Rtb7WEKSncm1",
            "Alice",
            "Johnson",
            "alicej",
            "alice.johnson@example.com",
            true,
            null,
            null,
            null,
            null,
            null,
            "GBP",
            "en",
            "Europe/London",
            "dark",
            "dark",
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now(),
            null
    );

    when(userService.updateUserPreferences(eq("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1"), any(UserDto.class)))
            .thenReturn(updatedDto);

    // -------------------------
    // Act + Assert
    // -------------------------
    mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .put("/api/user/updatePreferences")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.currency").value("GBP"))
            .andExpect(jsonPath("$.preferredLanguage").value("en"))
            .andExpect(jsonPath("$.timezone").value("Europe/London"))
            .andExpect(jsonPath("$.theme").value("dark"));
  }

  @Test
  void testOnSuccessfulLogin_ReturnsOk() throws Exception {

    // -------------------------
    // Arrange
    // -------------------------
    when(authContext.getFirebaseUid())
            .thenReturn("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1");

    // onSuccessfulLogin returns void → just verify it’s called
    when(userService.onSuccessfulLogin("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1"))
            .thenReturn(null);

    // -------------------------
    // Act + Assert
    // -------------------------
    mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .post("/api/user/loginSuccess")
            )
            .andExpect(status().isOk());

    verify(userService, times(1))
            .onSuccessfulLogin("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1");
  }

}
