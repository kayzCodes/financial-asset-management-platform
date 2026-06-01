package uk.ac.rhul.cs3821.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs3821.entity.UserCrypto;

/**
 * This is the users' crypto repository.
 */
public interface UserCryptoRepository extends JpaRepository<UserCrypto, Long> {

  /**
   * Finds all cryptocurrency belonging to the specified user.
   *
   * @param userId the id of the user whose goals should be retrieved
   * @return a list of {@link UserCrypto} entities for the given user ID
   */
  List<UserCrypto> findByUserIdAndIsDeletedFalse(Long userId);
}
