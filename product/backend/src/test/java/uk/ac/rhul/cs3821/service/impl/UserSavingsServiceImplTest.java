package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.dto.AddSavingRequestDto;
import uk.ac.rhul.cs3821.dto.UserSavingsDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserGoal;
import uk.ac.rhul.cs3821.entity.UserSavings;
import uk.ac.rhul.cs3821.repository.UserGoalRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.repository.UserSavingsRepository;

class UserSavingsServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserGoalRepository userGoalRepository;

  @Mock
  private UserSavingsRepository userSavingsRepository;

  @InjectMocks
  private UserSavingsServiceImpl service;

  private User user;
  private UserGoal goal;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(1L);
    user.setFirebaseUid("uid123");

    goal = new UserGoal();
    goal.setId(10L);
    goal.setUser(user);
    goal.setCurrentAmount(new BigDecimal("100.00"));
  }

  // ✅ SUCCESS: add saving
  @Test
  void shouldAddSavingSuccessfully() {

    // Arrange
    AddSavingRequestDto request =
            new AddSavingRequestDto(10L, new BigDecimal("50.00"));

    // 🔥 MUST initialise state
    goal.setCurrentAmount(new BigDecimal("100.00"));

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));
    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.of(goal));

    UserSavings saved = UserSavings.builder()
            .id(100L)
            .user(user)
            .goal(goal)
            .amount(new BigDecimal("50.00"))
            .createdAt(LocalDateTime.now())
            .build();

    when(userSavingsRepository.save(any(UserSavings.class)))
            .thenReturn(saved);

    // Act
    UserSavingsDto result = service.addSaving("uid123", request);

    // Assert
    assertNotNull(result);

    // ✅ Correct type (Long)
    assertEquals(100L, result.id());

    // ✅ Goal ID mapped correctly
    assertEquals(10L, result.goalId());

    // ✅ BigDecimal comparison
    assertEquals(0, result.amount().compareTo(new BigDecimal("50.00")));

    // ✅ Goal updated correctly
    assertEquals(
            0,
            goal.getCurrentAmount().compareTo(new BigDecimal("150.00"))
    );

    // Verify interactions
    verify(userSavingsRepository).save(any(UserSavings.class));
    verify(userGoalRepository).save(goal);
  }

  // FAIL: user not found
  @Test
  void shouldThrowWhenUserNotFound() {

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> service.addSaving("uid123",
                    new AddSavingRequestDto(10L, new BigDecimal("50.00")))
    );

    assertEquals("User not found", ex.getMessage());
  }

  // FAIL: goal not found
  @Test
  void shouldThrowWhenGoalNotFound() {

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));
    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> service.addSaving("uid123",
                    new AddSavingRequestDto(10L, new BigDecimal("50.00")))
    );

    assertEquals("Goal not found", ex.getMessage());
  }

  // FAIL: ownership violation
  @Test
  void shouldThrowWhenGoalNotOwnedByUser() {

    User otherUser = new User();
    otherUser.setId(2L);
    goal.setUser(otherUser);

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));
    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.of(goal));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> service.addSaving("uid123",
                    new AddSavingRequestDto(10L, new BigDecimal("50.00")))
    );

    assertEquals("Goal does not belong to user", ex.getMessage());
  }

  // FAIL: invalid amount
  @Test
  void shouldThrowWhenAmountInvalid() {

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));
    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.of(goal));

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> service.addSaving("uid123",
                    new AddSavingRequestDto(10L, BigDecimal.ZERO))
    );

    assertEquals("Amount must be greater than zero", ex.getMessage());
  }

  // SUCCESS: get savings by goal
  @Test
  void shouldGetSavingsByGoal() {

    UserSavings saving = UserSavings.builder()
            .id(1L)
            .user(user)
            .goal(goal)
            .amount(new BigDecimal("25.00"))
            .build();

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));
    when(userGoalRepository.findById(10L))
            .thenReturn(Optional.of(goal));
    when(userSavingsRepository.findByGoalId(10L))
            .thenReturn(List.of(saving));

    List<UserSavingsDto> result =
            service.getSavingsByGoal("uid123", 10L);

    assertEquals(1, result.size());
  }

  // SUCCESS: delete saving
  @Test
  void shouldDeleteSavingAndUpdateGoal() {

    UserSavings saving = UserSavings.builder()
            .id(1L)
            .user(user)
            .goal(goal)
            .amount(new BigDecimal("50.00"))
            .build();

    when(userRepository.findByFirebaseUid("uid123"))
            .thenReturn(Optional.of(user));
    when(userSavingsRepository.findById(1L))
            .thenReturn(Optional.of(saving));

    UserSavingsDto result =
            service.deleteSaving("uid123", 1L);

    assertNotNull(result);

    // verify goal reduced
    assertEquals(
            0,
            goal.getCurrentAmount().compareTo(new BigDecimal("50.00"))
    );

    verify(userSavingsRepository).delete(saving);
    verify(userGoalRepository).save(goal);
  }
}