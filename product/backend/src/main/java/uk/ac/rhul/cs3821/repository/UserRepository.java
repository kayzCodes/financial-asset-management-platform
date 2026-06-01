package uk.ac.rhul.cs3821.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs3821.entity.User;

/**
 * This is the user repository.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a user using their Firebase UID.
   *
   * @param firebaseUid the UID provided by Firebase Authentication
   * @return an Optional containing the user if found
   */
  Optional<User> findByFirebaseUid(String firebaseUid);

  /**
   * Finds a user by email.
   */
  Optional<User> findByEmail(String email);
}
