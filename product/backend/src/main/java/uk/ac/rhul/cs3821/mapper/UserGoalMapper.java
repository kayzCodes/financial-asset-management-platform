package uk.ac.rhul.cs3821.mapper;

import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserGoalDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserGoal;

/**
 * Mapper class for converting between UserGoal and UserGoalDto.
 */
public class UserGoalMapper {

  /**
   * Maps a UserGoal entity to a UserGoalDto.
   *
   * @param goal the UserGoal entity
   * @return the corresponding UserGoalDto
   */
  public static UserGoalDto mapToUserGoalDto(UserGoal goal) {
    if (goal == null) {
      return null;
    }

    UserDto userDto = null;
    if (goal.getUser() != null) {
      userDto = UserMapper.mapToUserDto(goal.getUser());
    }

    return new UserGoalDto(
            goal.getId(),
            userDto,
            goal.getGoalTitle(),
            goal.getCurrentAmount(),
            goal.getTargetAmount(),
            goal.getDeadline(),
            goal.getDescription(),
            goal.getCreatedAt()
    );
  }

  /**
   * Maps a UserGoalDto to a UserGoal entity.
   *
   * @param dto the UserGoalDto
   * @return a UserGoal entity
   */
  public static UserGoal mapToUserGoal(UserGoalDto dto) {
    if (dto == null) {
      return null;
    }

    UserGoal goal = new UserGoal();
    goal.setId(dto.getId());
    goal.setGoalTitle(dto.getGoalTitle());
    goal.setCurrentAmount(dto.getCurrentAmount());
    goal.setTargetAmount(dto.getTargetAmount());
    goal.setDeadline(dto.getDeadline());
    goal.setDescription(dto.getDescription());
    goal.setCreatedAt(dto.getCreatedAt());

    // Convert embedded UserDto → User entity
    if (dto.getUser() != null) {
      User user = UserMapper.mapToUser(dto.getUser());
      goal.setUser(user);
    }

    return goal;
  }
}
