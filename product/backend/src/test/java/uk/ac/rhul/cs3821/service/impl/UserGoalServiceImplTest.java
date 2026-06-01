package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserGoalDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserGoal;
import uk.ac.rhul.cs3821.repository.UserGoalRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.service.UserService;

public class UserGoalServiceImplTest {

  private final String firebaseUid = "firebase123";

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserService userService;

  @Mock
  private UserGoalRepository userGoalRepository;

  @InjectMocks
  private UserGoalServiceImpl userGoalService;

  private UserDto userDto;
  private User userEntity;
  private UserGoalDto goalDto;
  private UserGoal goalEntity;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    userDto = new UserDto();
    userDto.setId(10L);
    userDto.setFirebaseUid("firebase123");
    userDto.setFirstName("Alice");
    userDto.setLastName("Johnson");
    userDto.setUsername("alicej");
    userDto.setEmail("alice@example.com");

    userEntity = new User();
    userEntity.setId(10L);
    userEntity.setFirebaseUid("firebase123");
    userEntity.setUsername("alicej");

    goalDto = new UserGoalDto(
            null,
            userDto,
            "Save for Laptop",
            new BigDecimal(1000.00),
            new BigDecimal(1200.0),
            LocalDateTime.now().plusDays(30),
            "Need a MacBook",
            null
    );

    goalEntity = new UserGoal(
            1L,
            userEntity,
            "Save for Laptop",
            new BigDecimal(1000.00),
            new BigDecimal(1200.0),
            goalDto.getDeadline(),
            "Need a MacBook",
            LocalDateTime.now()
    );
  }

  // createGoalForUser — success case
  @Test
  void testCreateGoalForUser_Success() {

    when(userService.getUserByFirebaseUid("firebase123"))
            .thenReturn(userDto);

    when(userGoalRepository.save(any(UserGoal.class)))
            .thenReturn(goalEntity);

    UserGoalDto result = userGoalService.createGoalForUser("firebase123", goalDto);

    assertNotNull(result);
    assertEquals("Save for Laptop", result.getGoalTitle());
    assertEquals(0, result.getCurrentAmount().compareTo(new BigDecimal("1000.00")));
    assertEquals(0, result.getTargetAmount().compareTo(new BigDecimal("1200.00")));
    assertEquals("Need a MacBook", result.getDescription());
    verify(userGoalRepository, times(1)).save(any(UserGoal.class));
  }

  // createGoalForUser — user not found
  @Test
  void testCreateGoalForUser_UserNotFound() {

    when(userService.getUserByFirebaseUid("firebase123"))
            .thenReturn(null);

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userGoalService.createGoalForUser("firebase123", goalDto));

    assertEquals("User not found for Firebase UID: firebase123", ex.getMessage());
  }

  // getAllUserGoalsByFirebaseUid — success
  @Test
  void testGetAllUserGoalsByFirebaseUid_Success() {

    when(userService.getUserByFirebaseUid("firebase123"))
            .thenReturn(userDto);

    when(userGoalRepository.findByUserId(10L))
            .thenReturn(Arrays.asList(goalEntity));

    List<UserGoalDto> result = userGoalService.getAllUserGoalsByFirebaseUid("firebase123");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Save for Laptop", result.get(0).getGoalTitle());
  }

  // getAllUserGoalsByFirebaseUid — user not found
  @Test
  void testGetAllUserGoalsByFirebaseUid_UserNotFound() {

    when(userService.getUserByFirebaseUid("firebase123"))
            .thenReturn(null);

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userGoalService.getAllUserGoalsByFirebaseUid("firebase123"));

    assertEquals("User not found for Firebase UID: firebase123", ex.getMessage());
  }

  @Test
  void updateGoal_successfullyUpdatesGoal() {
    // -------------------------
    // Arrange
    // -------------------------
    String firebaseUid = "firebase123";

    User user = new User();
    user.setId(1L);
    user.setFirebaseUid(firebaseUid);

    UserGoal existingGoal = new UserGoal();
    existingGoal.setId(10L);
    existingGoal.setUser(user);
    existingGoal.setGoalTitle("Old title");
    existingGoal.setDescription("Old desc");
    existingGoal.setTargetAmount(new BigDecimal(1000.0));
    existingGoal.setCurrentAmount(new BigDecimal(200.0));
    existingGoal.setDeadline(LocalDateTime.now().plusMonths(6));

    UserGoalDto updateDto = new UserGoalDto();
    updateDto.setGoalTitle("New title");
    updateDto.setDescription("New desc");
    updateDto.setTargetAmount(new BigDecimal(5000.0));
    updateDto.setCurrentAmount(new BigDecimal(750.0));
    updateDto.setDeadline(LocalDateTime.now().plusMonths(12));

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.of(existingGoal));

    when(userGoalRepository.save(any(UserGoal.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // -------------------------
    // Act
    // -------------------------
    UserGoalDto result =
            userGoalService.updateGoal(firebaseUid, 10L, updateDto);

    // -------------------------
    // Assert
    // -------------------------
    assertNotNull(result);
    assertEquals("New title", result.getGoalTitle());
    assertEquals("New desc", result.getDescription());
    assertEquals(0, result.getTargetAmount().compareTo(new BigDecimal("5000.00")));
    assertEquals(0, result.getCurrentAmount().compareTo(new BigDecimal("750.00")));
    assertEquals(updateDto.getDeadline(), result.getDeadline());

    verify(userRepository, times(1))
            .findByFirebaseUid(firebaseUid);

    verify(userGoalRepository, times(1))
            .findById(10L);

    verify(userGoalRepository, times(1))
            .save(existingGoal);
  }

  @Test
  void deleteGoal_success() {
    // -------------------------
    // Arrange
    // -------------------------
    String firebaseUid = "firebase123";

    User user = new User();
    user.setId(1L);
    user.setFirebaseUid(firebaseUid);

    UserGoal goal = new UserGoal();
    goal.setId(10L);
    goal.setUser(user);
    goal.setGoalTitle("Save for car");

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(user));

    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.of(goal));

    // -------------------------
    // Act
    // -------------------------
    UserGoalDto result =
            userGoalService.deleteGoal(firebaseUid, 10L);

    // -------------------------
    // Assert
    // -------------------------
    assertNotNull(result);
    assertEquals("Save for car", result.getGoalTitle());

    verify(userRepository, times(1))
            .findByFirebaseUid(firebaseUid);

    verify(userGoalRepository, times(1))
            .findById(10L);

    verify(userGoalRepository, times(1))
            .delete(goal);
  }

  @Test
  void updateGoal_throws_whenUserNotFound() {
    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userGoalService.updateGoal(firebaseUid, 10L, new UserGoalDto())
    );

    assertEquals(
            "User not found for Firebase UID: " + firebaseUid,
            ex.getMessage()
    );

    verify(userGoalRepository, never()).findById(any());
    verify(userGoalRepository, never()).save(any());
  }

  @Test
  void updateGoal_throws_whenGoalNotFound() {
    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(userEntity));

    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userGoalService.updateGoal(firebaseUid, 10L, new UserGoalDto())
    );

    assertEquals("Goal not found for ID: 10", ex.getMessage());

    verify(userGoalRepository, never()).save(any());
  }

  @Test
  void updateGoal_throws_whenGoalDoesNotBelongToUser() {
    User otherUser = new User();
    otherUser.setId(99L);

    UserGoal goal = new UserGoal();
    goal.setId(10L);
    goal.setUser(otherUser);

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(userEntity));

    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.of(goal));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userGoalService.updateGoal(firebaseUid, 10L, new UserGoalDto())
    );

    assertEquals("Goal does not belong to the authenticated user", ex.getMessage());

    verify(userGoalRepository, never()).save(any());
  }

  @Test
  void deleteGoal_throws_whenUserNotFound() {
    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userGoalService.deleteGoal(firebaseUid, 10L)
    );

    assertEquals(
            "User not found for Firebase UID: " + firebaseUid,
            ex.getMessage()
    );

    verify(userGoalRepository, never()).findById(any());
    verify(userGoalRepository, never()).delete(any());
  }

  @Test
  void deleteGoal_throws_whenGoalNotFound() {
    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(userEntity));

    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userGoalService.deleteGoal(firebaseUid, 10L)
    );

    assertEquals("Goal not found for ID: 10", ex.getMessage());

    verify(userGoalRepository, never()).delete(any());
  }

  @Test
  void deleteGoal_throws_whenGoalDoesNotBelongToUser() {
    User otherUser = new User();
    otherUser.setId(99L);

    UserGoal goal = new UserGoal();
    goal.setId(10L);
    goal.setUser(otherUser);

    when(userRepository.findByFirebaseUid(firebaseUid))
            .thenReturn(Optional.of(userEntity));

    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.of(goal));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> userGoalService.deleteGoal(firebaseUid, 10L)
    );

    assertEquals("Goal does not belong to the authenticated user", ex.getMessage());

    verify(userGoalRepository, never()).delete(any());
  }


}
