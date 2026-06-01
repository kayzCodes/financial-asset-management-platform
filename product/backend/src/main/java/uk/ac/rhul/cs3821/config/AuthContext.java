package uk.ac.rhul.cs3821.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper component to retrieve authentication details from the application security context.
 * Abstracts direct access to the SecurityContextHolder.
 */
@Component
public class AuthContext {

  /**
   * Retrieves the Firebase UID (User Name) of the currently authenticated user.
   *
   * @return the unique identifier of the user from the security context.
   */
  public String getFirebaseUid() {
    return SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();
  }
}

