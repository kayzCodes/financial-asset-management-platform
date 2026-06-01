package uk.ac.rhul.cs3821.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserGoalDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserGoal;

public class UserGoalMapperTest {

  @Test
  void testMapToUserGoalDto() {

    LocalDateTime deadline = LocalDateTime.now().plusDays(10);
    LocalDateTime created = LocalDateTime.now().minusDays(1);

    User user = new User();
    user.setId(1L);
    user.setFirebaseUid("firebase123");
    user.setFirstName("Alice");
    user.setLastName("Johnson");
    user.setUsername("alicej");
    user.setEmail("alice@example.com");

    UserGoal goal = new UserGoal(
            10L,
            user,
            "Save for Trip",
            new BigDecimal(1000.00),
            new BigDecimal(1500.00),
            deadline,
            "Trip to Japan",
            created
    );

    UserGoalDto dto = UserGoalMapper.mapToUserGoalDto(goal);

    assertNotNull(dto);
    assertEquals(10L, dto.getId());
    assertEquals("Save for Trip", dto.getGoalTitle());
    assertEquals(0, dto.getCurrentAmount().compareTo(new BigDecimal("1000.00")));
    assertEquals(0, dto.getTargetAmount().compareTo(new BigDecimal("1500.00")));
    assertEquals(deadline, dto.getDeadline());
    assertEquals("Trip to Japan", dto.getDescription());
    assertEquals(created, dto.getCreatedAt());

    // Validate mapped user
    assertNotNull(dto.getUser());
    assertEquals(1L, dto.getUser().getId());
    assertEquals("Alice", dto.getUser().getFirstName());
    assertEquals("alicej", dto.getUser().getUsername());
  }

  @Test
  void testMapToUserGoalDto_NullInput() {
    assertNull(UserGoalMapper.mapToUserGoalDto(null));
  }

  @Test
  void testMapToUserGoal() {

    LocalDateTime deadline = LocalDateTime.now().plusDays(20);
    LocalDateTime created = LocalDateTime.now();

    UserDto userDto = new UserDto();
    userDto.setId(2L);
    userDto.setFirebaseUid("firebase456");
    userDto.setFirstName("Bob");
    userDto.setLastName("Smith");
    userDto.setUsername("bobsmith");
    userDto.setEmail("bob@example.com");

    UserGoalDto dto = new UserGoalDto(
            5L,
            userDto,
            "Emergency Fund",
            new BigDecimal(2000.00),
            new BigDecimal(3000.00),
            deadline,
            "Save for unexpected expenses",
            created
    );

    UserGoal goal = UserGoalMapper.mapToUserGoal(dto);

    assertNotNull(goal);
    assertEquals(5L, goal.getId());
    assertEquals("Emergency Fund", goal.getGoalTitle());
    assertEquals(0, goal.getCurrentAmount().compareTo(new BigDecimal("2000.00")));
    assertEquals(0, goal.getTargetAmount().compareTo(new BigDecimal("3000.00")));
    assertEquals(deadline, goal.getDeadline());
    assertEquals("Save for unexpected expenses", goal.getDescription());
    assertEquals(created, goal.getCreatedAt());

    // Validate mapped user
    assertNotNull(goal.getUser());
    assertEquals("bobsmith", goal.getUser().getUsername());
    assertEquals("Bob", goal.getUser().getFirstName());
  }

  @Test
  void testMapToUserGoal_NullInput() {
    assertNull(UserGoalMapper.mapToUserGoal(null));
  }
}
