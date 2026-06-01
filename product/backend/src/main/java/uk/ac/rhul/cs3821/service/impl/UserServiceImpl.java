package uk.ac.rhul.cs3821.service.impl;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserRegistrationDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.mapper.UserMapper;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.service.UserService;

/**
 * Service implementation for managing user operations using Firebase authentication.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  /**
   * Registers a new user after Firebase authentication.
   *
   * @param registrationDto contains Firebase UID, email, first name, last name, and username
   * @return the created user as a {@link UserDto}
   */
  @Override
  public UserDto registerUser(UserRegistrationDto registrationDto) {

    // UID MUST come from the FRONTEND at registration (because registerUser is NOT protected)
    if (registrationDto.getFirebaseUid() == null || registrationDto.getFirebaseUid().isBlank()) {
      throw new RuntimeException("Firebase UID missing in request.");
    }

    // Prevent duplicates
    userRepository.findByFirebaseUid(registrationDto.getFirebaseUid())
            .ifPresent(u -> {
              throw new RuntimeException("User already exists");
            });

    User user = new User();
    user.setFirebaseUid(registrationDto.getFirebaseUid());
    user.setFirstName(registrationDto.getFirstName());
    user.setLastName(registrationDto.getLastName());
    user.setUsername(registrationDto.getUsername());
    user.setEmail(registrationDto.getEmail());

    // Done by the System
    user.setActive(true);
    user.setCurrency("GBP");
    user.setRole("standard");
    user.setLastLoginAt(LocalDateTime.now());

    User savedUser = userRepository.save(user);

    return UserMapper.mapToUserDto(savedUser);
  }

  /**
   * Updates a user's profile using their Firebase UID.
   *
   * @param firebaseUid the Firebase UID of the user
   * @param profileDto  updated profile data
   * @return updated user as {@link UserDto}
   */
  @Override
  public UserDto updateUserProfile(String firebaseUid, UserDto profileDto) {

    System.out.println("DTO: " + profileDto);

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    System.out.println("BEFORE: " + user);

    if (profileDto.getFirstName() != null) {
      user.setFirstName(profileDto.getFirstName());
    }

    if (profileDto.getLastName() != null) {
      user.setLastName(profileDto.getLastName());
    }

    if (profileDto.getUsername() != null) {
      user.setUsername(profileDto.getUsername());
    }

    if (profileDto.getBio() != null) {
      user.setBio(profileDto.getBio());
    }

    if (profileDto.getPhoneNumber() != null) {
      user.setPhoneNumber(profileDto.getPhoneNumber());
    }

    if (profileDto.getProfilePictureUrl() != null) {
      user.setProfilePictureUrl(profileDto.getProfilePictureUrl());
    }

    if (profileDto.getPreferredLanguage() != null) {
      user.setPreferredLanguage(profileDto.getPreferredLanguage());
    }

    if (profileDto.getTimezone() != null) {
      user.setTimezone(profileDto.getTimezone());
    }

    user.setUpdatedAt(LocalDateTime.now());

    User updatedUser = userRepository.save(user);
    System.out.println("AFTER: " + updatedUser);

    return UserMapper.mapToUserDto(updatedUser);
  }


  /**
   * Retrieves a user by their Firebase UID.
   *
   * @param firebaseUid the Firebase authentication UID
   * @return the found user as a {@link UserDto}
   */
  @Override
  public UserDto getUserByFirebaseUid(String firebaseUid) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    userRepository.save(user);

    return UserMapper.mapToUserDto(user);
  }

  /**
   * Returns a user by Firebase UID, or null if not found.
   *
   * @param firebaseUid the Firebase UID
   * @return the user as {@link UserDto}, or null if not found
   */
  @Override
  public UserDto checkUserByFirebaseUid(String firebaseUid) {
    return userRepository.findByFirebaseUid(firebaseUid)
            .map(UserMapper::mapToUserDto)
            .orElse(null);
  }

  /**
   * Updates a user's profile information based on their Firebase UID.
   * * Modifies personal details including name, username, bio, and contact info while
   * automatically updating the system timestamp. Restricts changes to user-editable
   * fields to maintain account integrity.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param userDto     the DTO containing updated profile details.
   * @return the updated {@link UserDto} after persistence.
   * @throws RuntimeException if the user is not found.
   */
  @Override
  public UserDto updateUser(String firebaseUid, UserDto userDto) {

    // 1. Fetch user by Firebase UID
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException(
                    "User not found for Firebase UID: " + firebaseUid));

    // 2. Update allowed fields ONLY (settings page)
    user.setFirstName(userDto.getFirstName());
    user.setLastName(userDto.getLastName());
    user.setUsername(userDto.getUsername());
    user.setProfilePictureUrl(userDto.getProfilePictureUrl());
    user.setBio(userDto.getBio());
    user.setPhoneNumber(userDto.getPhoneNumber());


    // 3. System-managed fields
    user.setUpdatedAt(LocalDateTime.now());

    // 4. Persist + return DTO
    User updatedUser = userRepository.save(user);
    return UserMapper.mapToUserDto(updatedUser);
  }

  /**
   * Updates a user's regional and localization preferences.
   * * Modifies specific fields including currency, language, and timezone while
   * maintaining existing core identity data. Validates user existence via
   * Firebase UID before persisting changes.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param userDto     the DTO containing the new preference settings.
   * @return the updated {@link UserDto} after database persistence.
   * @throws RuntimeException if the user is not found.
   */
  public UserDto updateUserPreferences(String firebaseUid, UserDto userDto) {
    // 1. Fetch user by Firebase UID
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException(
                    "User not found for Firebase UID: " + firebaseUid));

    // 2. Update allowed fields ONLY (preferences page)
    user.setCurrency(userDto.getCurrency());
    user.setPreferredLanguage(userDto.getPreferredLanguage());
    user.setTimezone(userDto.getTimezone());
    user.setTheme(userDto.getTheme());

    // 3. Persist + return DTO
    User updatedUser = userRepository.save(user);
    return UserMapper.mapToUserDto(updatedUser);
  }

  /**
   * Updates the user's last login timestamp upon a successful authentication event.
   * Locates the user by their Firebase UID, records the current system time as
   * the most recent login, and persists the update to the database.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @return the updated {@link UserDto} reflecting the new login timestamp.
   * @throws RuntimeException if no user is found for the provided UID.
   */
  public UserDto onSuccessfulLogin(String firebaseUid) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException(
                    "User not found for Firebase UID: " + firebaseUid));

    user.setLastLoginAt(LocalDateTime.now());
    User updatedUser = userRepository.save(user);

    return UserMapper.mapToUserDto(updatedUser);
  }
}
