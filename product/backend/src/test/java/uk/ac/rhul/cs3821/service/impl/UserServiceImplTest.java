package uk.ac.rhul.cs3821.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserRegistrationDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.repository.UserRepository;

class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  private User mockUser;
  private UserRegistrationDto registrationDto;
  private UserDto profileUpdateDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    mockUser = new User();
    mockUser.setId(1L);
    mockUser.setFirebaseUid("firebase123");
    mockUser.setFirstName("Alice");
    mockUser.setLastName("Johnson");
    mockUser.setUsername("alicej");
    mockUser.setEmail("alice.johnson@example.com");
    mockUser.setActive(true);
    mockUser.setRole("standard");
    mockUser.setCurrency("USD");

    registrationDto = new UserRegistrationDto();
    registrationDto.setFirebaseUid("firebase123");
    registrationDto.setFirstName("Alice");
    registrationDto.setLastName("Johnson");
    registrationDto.setUsername("alicej");
    registrationDto.setEmail("alice.johnson@example.com");

    profileUpdateDto = new UserDto();
    profileUpdateDto.setBio("New bio");
    profileUpdateDto.setPhoneNumber("1234567890");
    profileUpdateDto.setPreferredLanguage("en");
    profileUpdateDto.setTimezone("Europe/London");
    profileUpdateDto.setTheme("system");
    profileUpdateDto.setProfilePictureUrl("http://example.com/pic.png");
  }

  //  Registration success
  @Test
  void testRegisterUser_Success() {
    when(userRepository.findByFirebaseUid("firebase123"))
            .thenReturn(Optional.empty());

    when(userRepository.save(any(User.class)))
            .thenReturn(mockUser);

    UserDto result = userService.registerUser(registrationDto);

    assertNotNull(result);
    assertEquals("alicej", result.getUsername());
    verify(userRepository, times(1)).save(any(User.class));
  }

  //  Registration fails if UID missing
  @Test
  void testRegisterUser_MissingUid_ThrowsException() {
    registrationDto.setFirebaseUid(null);

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.registerUser(registrationDto));

    assertEquals("Firebase UID missing in request.", ex.getMessage());
  }

  //  Registration fails if duplicate
  @Test
  void testRegisterUser_ExistingUser_ThrowsException() {
    when(userRepository.findByFirebaseUid("firebase123"))
            .thenReturn(Optional.of(mockUser));

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.registerUser(registrationDto));

    assertEquals("User already exists", ex.getMessage());
  }

  //  Update profile success
  @Test
  void testUpdateUserProfile_Success() {

    when(userRepository.findByFirebaseUid("firebase123"))
            .thenReturn(Optional.of(mockUser));

    when(userRepository.save(any(User.class)))
            .thenReturn(mockUser);

    UserDto result = userService.updateUserProfile("firebase123", profileUpdateDto);

    assertNotNull(result);
    verify(userRepository, times(1)).save(any(User.class));
  }

  //  Update fails if no user exists
  @Test
  void testUpdateUserProfile_UserNotFound() {

    when(userRepository.findByFirebaseUid("firebase123"))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.updateUserProfile("firebase123", profileUpdateDto));

    assertEquals("User not found", ex.getMessage());
  }

  //  Get user by UID success
  @Test
  void testGetUserByFirebaseUid_Success() {

    when(userRepository.findByFirebaseUid("firebase123"))
            .thenReturn(Optional.of(mockUser));

    when(userRepository.save(any(User.class)))
            .thenReturn(mockUser);

    UserDto result = userService.getUserByFirebaseUid("firebase123");

    assertNotNull(result);
    assertEquals("Alice", result.getFirstName());
  }

  //  Get user fails
  @Test
  void testGetUserByFirebaseUid_NotFound() {

    when(userRepository.findByFirebaseUid("firebase123"))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.getUserByFirebaseUid("firebase123"));

    assertEquals("User not found", ex.getMessage());
  }

  // checkUserByFirebaseUid — success, user found
  @Test
  void testCheckUserByFirebaseUid_UserFound() {

    when(userRepository.findByFirebaseUid("firebase123"))
            .thenReturn(Optional.of(mockUser));

    UserDto result = userService.checkUserByFirebaseUid("firebase123");

    assertNotNull(result);
    assertEquals("alicej", result.getUsername());
    verify(userRepository, times(1)).findByFirebaseUid("firebase123");
  }

  // checkUserByFirebaseUid — returns null if user not found
  @Test
  void testCheckUserByFirebaseUid_UserNotFound() {

    when(userRepository.findByFirebaseUid("firebase123"))
            .thenReturn(Optional.empty());

    UserDto result = userService.checkUserByFirebaseUid("firebase123");

    assertEquals(null, result);
    verify(userRepository, times(1)).findByFirebaseUid("firebase123");
  }

  @Test
  void updateUser_success() {
    // -------------------------
    // Arrange
    // -------------------------
    String firebaseUid = "firebase123";

    User existingUser = new User();
    existingUser.setId(1L);
    existingUser.setFirebaseUid(firebaseUid);
    existingUser.setFirstName("Old");
    existingUser.setLastName("Name");
    existingUser.setUsername("olduser");
    existingUser.setProfilePictureUrl("old.png");
    existingUser.setBio("old bio");
    existingUser.setPhoneNumber("000");
    existingUser.setUpdatedAt(LocalDateTime.now().minusDays(1));

    UserDto updateDto = new UserDto();
    updateDto.setFirstName("New");
    updateDto.setLastName("User");
    updateDto.setUsername("newuser");
    updateDto.setProfilePictureUrl("new.png");
    updateDto.setBio("new bio");
    updateDto.setPhoneNumber("123456789");

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(existingUser));

    when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // -------------------------
    // Act
    // -------------------------
    UserDto result =
            userService.updateUser(firebaseUid, updateDto);

    // -------------------------
    // Assert
    // -------------------------
    assertNotNull(result);
    assertEquals("New", result.getFirstName());
    assertEquals("User", result.getLastName());
    assertEquals("newuser", result.getUsername());
    assertEquals("new.png", result.getProfilePictureUrl());
    assertEquals("new bio", result.getBio());
    assertEquals("123456789", result.getPhoneNumber());

    verify(userRepository, times(1))
            .findByFirebaseUid(firebaseUid);

    verify(userRepository, times(1))
            .save(existingUser);
  }

  @Test
  void updateUserPreferences_success() {
    // -------------------------
    // Arrange
    // -------------------------
    String firebaseUid = "firebase123";

    User existingUser = new User();
    existingUser.setId(1L);
    existingUser.setFirebaseUid(firebaseUid);
    existingUser.setCurrency("USD");
    existingUser.setPreferredLanguage("en");
    existingUser.setTimezone("UTC");
    existingUser.setTheme("dark");

    UserDto preferencesDto = new UserDto();
    preferencesDto.setCurrency("GBP");
    preferencesDto.setPreferredLanguage("fr");
    preferencesDto.setTimezone("Europe/London");
    preferencesDto.setTheme("light"); // should be ignored

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(existingUser));

    when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // -------------------------
    // Act
    // -------------------------
    UserDto result =
            userService.updateUserPreferences(firebaseUid, preferencesDto);

    // -------------------------
    // Assert
    // -------------------------
    assertNotNull(result);
    assertEquals("GBP", result.getCurrency());
    assertEquals("fr", result.getPreferredLanguage());
    assertEquals("Europe/London", result.getTimezone());

    // theme must NOT change
    assertEquals("light", result.getTheme());

    verify(userRepository, times(1))
            .findByFirebaseUid(firebaseUid);

    verify(userRepository, times(1))
            .save(existingUser);
  }

  @Test
  void onSuccessfulLogin_updatesLastLoginAndReturnsDto() {
    // -------------------------
    // Arrange
    // -------------------------
    String firebaseUid = "firebase123";

    User user = new User();
    user.setId(1L);
    user.setFirebaseUid(firebaseUid);
    user.setLastLoginAt(LocalDateTime.now().minusDays(1));

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // -------------------------
    // Act
    // -------------------------
    UserDto result =
            userService.onSuccessfulLogin(firebaseUid);

    // -------------------------
    // Assert
    // -------------------------
    assertNotNull(result);
    assertEquals(firebaseUid, result.getFirebaseUid());

    // lastLoginAt must be updated
    assertNotNull(user.getLastLoginAt());
    assertTrue(
            user.getLastLoginAt().isAfter(LocalDateTime.now().minusMinutes(1))
    );

    verify(userRepository, times(1))
            .findByFirebaseUid(firebaseUid);

    verify(userRepository, times(1))
            .save(user);
  }

}
