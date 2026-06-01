package uk.ac.rhul.cs3821.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the UserGoal entity class.
 */
public class UserGoalTest {

  @Test
  void testNoArgsConstructorAndSetters() {

    UserGoal goal = new UserGoal();

    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername("alicej");

    LocalDateTime deadline = LocalDateTime.now().plusDays(30);
    LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

    goal.setId(10L);
    goal.setUser(mockUser);
    goal.setGoalTitle("Save for Laptop");
    goal.setTargetAmount(BigDecimal.valueOf(1200.50));
    goal.setDeadline(deadline);
    goal.setDescription("Need a MacBook Pro");
    goal.setCreatedAt(createdAt);

    assertEquals(10L, goal.getId());
    assertEquals(mockUser, goal.getUser());
    assertEquals("Save for Laptop", goal.getGoalTitle());
    assertEquals(0, goal.getTargetAmount().compareTo(new BigDecimal("1200.50")));
    assertEquals(deadline, goal.getDeadline());
    assertEquals("Need a MacBook Pro", goal.getDescription());
    assertEquals(createdAt, goal.getCreatedAt());
  }

  @Test
  void testAllArgsConstructor() {

    User mockUser = new User();
    mockUser.setId(2L);
    mockUser.setUsername("bob");

    LocalDateTime deadline = LocalDateTime.now().plusDays(60);
    LocalDateTime createdAt = LocalDateTime.now();

    UserGoal goal = new UserGoal(
            5L,
            mockUser,
            "Travel Fund",
            new BigDecimal("2500.00"),
            new BigDecimal("3000.00"),
            deadline,
            "Save for a trip to Japan",
            createdAt
    );

    assertEquals(5L, goal.getId());
    assertEquals(mockUser, goal.getUser());
    assertEquals("Travel Fund", goal.getGoalTitle());
    assertEquals(0, goal.getCurrentAmount().compareTo(new BigDecimal("2500.00")));
    assertEquals(0, goal.getTargetAmount().compareTo(new BigDecimal("3000.00")));
    assertEquals(deadline, goal.getDeadline());
    assertEquals("Save for a trip to Japan", goal.getDescription());
    assertEquals(createdAt, goal.getCreatedAt());
  }
}
