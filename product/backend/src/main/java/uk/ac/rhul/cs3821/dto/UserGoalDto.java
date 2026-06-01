package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for UserGoal entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGoalDto {

  private Long id;
  private UserDto user;
  private String goalTitle;
  private BigDecimal currentAmount;
  private BigDecimal targetAmount;
  private LocalDateTime deadline;
  private String description;
  private LocalDateTime createdAt;
}
