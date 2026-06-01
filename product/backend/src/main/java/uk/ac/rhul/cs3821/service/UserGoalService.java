package uk.ac.rhul.cs3821.service;

import java.util.List;
import uk.ac.rhul.cs3821.dto.UserGoalDto;

/**
 * The user goal service interface.
 */
public interface UserGoalService {

  /**
   * Creates a new goal for the authenticated user.
   *
   * @param firebaseUid the Firebase UID of the user creating the goal
   * @param goalDto     the goal details from the client
   * @return saved goal as UserGoalDto
   */
  UserGoalDto createGoalForUser(String firebaseUid, UserGoalDto goalDto);


  /**
   * Retrieves all goals belonging to a specific user.
   *
   * @param firebaseUid the Firebase UID of the user
   * @return a list of UserGoalDto objects
   */
  List<UserGoalDto> getAllUserGoalsByFirebaseUid(String firebaseUid);

  /**
   * Updates an existing goal holding for the specified user.
   *
   * @param firebaseUid the firebase UID of the user
   * @param holderId    the ID of the Goal to update
   * @param userGoalDto the updated goal data
   * @return the updated goal data
   */
  UserGoalDto updateGoal(String firebaseUid, Long holderId, UserGoalDto userGoalDto);

  /**
   * Deletes a specific user goal after verifying ownership.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param id          the primary key of the goal to be deleted.
   * @return the {@link UserGoalDto} of the removed goal.
   */
  UserGoalDto deleteGoal(String firebaseUid, Long id);
}
