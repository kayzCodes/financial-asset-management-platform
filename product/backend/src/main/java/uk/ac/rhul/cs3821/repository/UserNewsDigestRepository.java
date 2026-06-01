package uk.ac.rhul.cs3821.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs3821.entity.UserNewsDigest;

/**
 * Repository for managing user news digests.
 * Supports retrieval of latest and non-expired digests per user.
 */
public interface UserNewsDigestRepository
        extends JpaRepository<UserNewsDigest, Long> {

  Optional<UserNewsDigest> findTopByUser_IdOrderByGeneratedAtDesc(Long userId);

  Optional<UserNewsDigest> findTopByUser_IdAndExpiresAtAfterOrderByGeneratedAtDesc(
          Long userId,
          LocalDateTime now
  );
}