package uk.ac.rhul.cs3821.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs3821.entity.UserGoal;

/**
 * The CRUD repository for User goal.
 */
public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {

  /**
   * Finds all goals belonging to the specified user.
   *
   * @param userId the ID of the user whose goals should be retrieved
   * @return a list of {@link UserGoal} entities for the given user ID
   */
  List<UserGoal> findByUserId(Long userId);
}
