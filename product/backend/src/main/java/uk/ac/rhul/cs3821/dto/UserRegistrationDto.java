package uk.ac.rhul.cs3821.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO used when registering a new user in the backend after Firebase creates the account.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {

  /**
   * The unique Firebase Authentication UID for this user.
   */
  private String firebaseUid;

  private String firstName;
  private String lastName;
  private String username;
  private String email;
}
