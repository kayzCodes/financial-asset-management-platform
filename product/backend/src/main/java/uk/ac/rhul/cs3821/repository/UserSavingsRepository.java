package uk.ac.rhul.cs3821.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs3821.entity.UserSavings;

/**
 * Repository for accessing user savings records.
 */
public interface UserSavingsRepository extends JpaRepository<UserSavings, Long> {

  List<UserSavings> findByUserId(Long userId);

  List<UserSavings> findByGoalId(Long goalId);
}
