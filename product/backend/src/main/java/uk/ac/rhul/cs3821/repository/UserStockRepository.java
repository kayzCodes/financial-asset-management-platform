package uk.ac.rhul.cs3821.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs3821.entity.UserStock;

/**
 * This is the user stock repository.
 */
public interface UserStockRepository extends JpaRepository<UserStock, Long> {


  List<UserStock> findByUserIdAndIsDeletedFalse(Long userId);

}
