package uk.ac.rhul.cs3821.mapper;

import uk.ac.rhul.cs3821.dto.UserSavingsDto;
import uk.ac.rhul.cs3821.entity.UserSavings;

/**
 * Mapper for UserSavings.
 */
public class UserSavingsMapper {

  /**
   * Maps UserSavings entity to UserSavingsDto.
   *
   * @param saving the entity
   * @return dto
   */
  public static UserSavingsDto mapToDto(UserSavings saving) {
    if (saving == null) {
      return null;
    }

    return new UserSavingsDto(
            saving.getId(),
            saving.getGoal().getId(),
            saving.getAmount(),
            saving.getCreatedAt()
    );
  }
}