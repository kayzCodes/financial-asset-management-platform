package uk.ac.rhul.cs3821.mapper;

import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.entity.User;

/**
 * Mapper class for converting between {@link User} and {@link UserDto}.
 */
public class UserMapper {

  /**
   * Maps a {@link User} entity to a {@link UserDto}.
   *
   * @param user the user entity
   * @return the corresponding UserDto, or null if the user is null
   */
  public static UserDto mapToUserDto(User user) {
    if (user == null) {
      return null;
    }

    return new UserDto(
            user.getId(),
            user.getFirebaseUid(),
            user.getFirstName(),
            user.getLastName(),
            user.getUsername(),
            user.getEmail(),
            user.isActive(),
            user.getEmailVerifiedAt(),
            user.getLastLoginAt(),
            user.getProfilePictureUrl(),
            user.getBio(),
            user.getPhoneNumber(),
            user.getCurrency(),
            user.getPreferredLanguage(),
            user.getTimezone(),
            user.getRole(),
            user.getTheme(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getDeletedAt()
    );
  }

  /**
   * Maps a {@link UserDto} to a {@link User} entity.
   *
   * @param dto the user data transfer object
   * @return the corresponding User entity, or null if the dto is null
   */
  public static User mapToUser(UserDto dto) {
    if (dto == null) {
      return null;
    }

    User user = new User();
    user.setId(dto.getId());
    user.setFirebaseUid(dto.getFirebaseUid());
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setActive(dto.isActive());
    user.setEmailVerifiedAt(dto.getEmailVerifiedAt());
    user.setLastLoginAt(dto.getLastLoginAt());
    user.setProfilePictureUrl(dto.getProfilePictureUrl());
    user.setBio(dto.getBio());
    user.setPhoneNumber(dto.getPhoneNumber());
    user.setCurrency(dto.getCurrency());
    user.setPreferredLanguage(dto.getPreferredLanguage());
    user.setTimezone(dto.getTimezone());
    user.setRole(dto.getRole());
    user.setTheme(dto.getTheme());
    user.setCreatedAt(dto.getCreatedAt());
    user.setUpdatedAt(dto.getUpdatedAt());
    user.setDeletedAt(dto.getDeletedAt());

    return user;
  }
}
