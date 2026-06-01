package uk.ac.rhul.cs3821.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserRegistrationDto;
import uk.ac.rhul.cs3821.service.UserService;

/**
 * Controller for user registration, profile updates, and retrieval.
 * Uses Firebase UID provided by the authentication filter.
 */
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  private final AuthContext authContext;

  /**
   * Registers a new user using the data provided from the frontend.
   *
   * @param registrationDto the registration details including Firebase UID
   * @return the created user
   */
  @PostMapping("/registerUser")
  public ResponseEntity<UserDto> registerUser(
          @RequestBody UserRegistrationDto registrationDto
  ) {
    UserDto savedUser = userService.registerUser(registrationDto);
    return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
  }

  /**
   * Updates the profile of the authenticated user.
   *
   * @param profileDto the updated profile data
   * @return the updated user
   */
  @PutMapping("/updateUserProfile")
  public ResponseEntity<UserDto> updateUserProfile(
          @RequestBody UserDto profileDto
  ) {

    String firebaseUid = authContext.getFirebaseUid();
    UserDto updatedUser = userService.updateUserProfile(firebaseUid, profileDto);
    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
  }

  /**
   * Retrieves the authenticated user's data.
   *
   * @return the user data
   */
  @GetMapping("/getUser")
  public ResponseEntity<UserDto> getUser() {
    String firebaseUid = authContext.getFirebaseUid();
    UserDto user = userService.getUserByFirebaseUid(firebaseUid);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Checks whether a user exists using their Firebase UID.
   *
   * @param firebaseUid the Firebase UID to search for
   * @return the user as {@link UserDto} or null if not found
   */
  @GetMapping("/checkUserByFirebaseUid/{firebaseUid}")
  public ResponseEntity<UserDto> checkUserByFirebaseUid(@PathVariable String firebaseUid) {
    UserDto user = userService.checkUserByFirebaseUid(firebaseUid);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Updates the currently authenticated user's profile information.
   * Uses the security context to identify the target user.
   *
   * @param userDto the data transfer object containing the updated user details.
   * @return the updated user profile wrapped in a ResponseEntity.
   */
  @PutMapping("/updateUser")
  public ResponseEntity<UserDto> updateUser(
          @RequestBody UserDto userDto
  ) {
    String firebaseUid = authContext.getFirebaseUid();
    UserDto updatedUser = userService.updateUser(firebaseUid, userDto);
    return ResponseEntity.ok(updatedUser);
  }

  /**
   * Updates the specific preference settings for the authenticated user.
   * Delegates the update logic to the user service using the caller's identity.
   *
   * @param userDto the data transfer object containing the updated preferences.
   * @return the fully updated user profile including the new preferences.
   */
  @PutMapping("/updatePreferences")
  public ResponseEntity<UserDto> updateUserPreferences(
          @RequestBody UserDto userDto
  ) {
    String firebaseUid = authContext.getFirebaseUid();
    UserDto updatedUser =
            userService.updateUserPreferences(firebaseUid, userDto);
    return ResponseEntity.ok(updatedUser);
  }

  /**
   * Triggers post-authentication logic for the current user.
   * Typically used to update the last-login timestamp or audit logs.
   *
   * @return a 200 OK response indicating the event was processed.
   */
  @PostMapping("/loginSuccess")
  public ResponseEntity<Void> onSuccessfulLogin() {
    String firebaseUid = authContext.getFirebaseUid();
    userService.onSuccessfulLogin(firebaseUid);
    return ResponseEntity.ok().build();
  }

}
