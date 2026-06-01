package uk.ac.rhul.cs3821.service;

import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserRegistrationDto;

/**
 * Provides operations related to user management using Firebase authentication.
 */
public interface UserService {

  /**
   * Registers a new user after Firebase authentication.
   *
   * @param registrationDto registration details including Firebase UID and profile fields
   * @return the created user
   */
  UserDto registerUser(UserRegistrationDto registrationDto);

  /**
   * Updates a user's profile data.
   *
   * @param firebaseUid the Firebase UID of the user
   * @param profileDto  the updated user details
   * @return the updated user
   */
  UserDto updateUserProfile(String firebaseUid, UserDto profileDto);

  /**
   * Retrieves a user using their Firebase UID.
   *
   * @param firebaseUid the Firebase UID
   * @return the matching user
   */
  UserDto getUserByFirebaseUid(String firebaseUid);

  /**
   * Checks whether a user exists using the Firebase UID.
   * Returns the user if found, or null if not found.
   *
   * @param firebaseUid the Firebase UID to search for
   * @return a {@link UserDto} or null if no user exists
   */
  UserDto checkUserByFirebaseUid(String firebaseUid);

  /**
   * Updates a user's profile details and contact information.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param userDto     the DTO containing updated profile data.
   * @return the updated {@link UserDto} after persistence.
   */
  UserDto updateUser(String firebaseUid, UserDto userDto);

  /**
   * Updates a user's regional and display settings.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param userDto     the DTO containing the updated preference values.
   * @return the updated {@link UserDto} following persistence.
   */
  UserDto updateUserPreferences(String firebaseUid, UserDto userDto);

  /**
   * Updates the user's last login timestamp upon successful authentication.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @return the updated {@link UserDto} reflecting the current login event.
   */
  UserDto onSuccessfulLogin(String firebaseUid);


}
