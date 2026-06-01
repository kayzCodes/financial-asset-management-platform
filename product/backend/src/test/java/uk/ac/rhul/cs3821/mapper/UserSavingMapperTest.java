package uk.ac.rhul.cs3821.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.dto.UserSavingsDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserGoal;
import uk.ac.rhul.cs3821.entity.UserSavings;

class UserSavingMapperTest {

  @Test
  void shouldMapEntityToDto() {

    User user = new User();
    user.setId(1L);

    UserGoal goal = new UserGoal();
    goal.setId(10L);
    goal.setUser(user);

    LocalDateTime now = LocalDateTime.now();

    UserSavings saving = UserSavings.builder()
            .id(100L)
            .user(user)
            .goal(goal)
            .amount(new BigDecimal("150.00"))
            .createdAt(now)
            .build();

    UserSavingsDto dto = UserSavingsMapper.mapToDto(saving);

    assertNotNull(dto);
    assertEquals(100L, dto.id());
    assertEquals(10L, dto.goalId());
    assertEquals(0, dto.amount().compareTo(new BigDecimal("150.00")));
    assertEquals(now, dto.createdAt());
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {

    UserSavingsDto dto = UserSavingsMapper.mapToDto(null);

    assertNull(dto);
  }
}