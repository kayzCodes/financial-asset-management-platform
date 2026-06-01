package uk.ac.rhul.cs3821.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.UserGoalDto;
import uk.ac.rhul.cs3821.service.UserGoalService;

/**
 * Controller for creating and retrieving user goals.
 */
@RestController
@RequestMapping("/api/userGoals")
@RequiredArgsConstructor
public class UserGoalController {

  private final UserGoalService userGoalService;
  private final AuthContext authContext;

  /**
   * Creates a new goal for the authenticated user.
   *
   * @param goalDto the goal data to create
   * @return created goal
   */
  @PostMapping("/createGoal")
  public ResponseEntity<UserGoalDto> createGoal(@RequestBody UserGoalDto goalDto) {

    String firebaseUid = authContext.getFirebaseUid();

    UserGoalDto createdGoal = userGoalService.createGoalForUser(firebaseUid, goalDto);

    return new ResponseEntity<>(createdGoal, HttpStatus.CREATED);
  }

  /**
   * Retrieves all goals for the authenticated user.
   *
   * @return list of goals
   */
  @GetMapping("/getGoals")
  public ResponseEntity<List<UserGoalDto>> getUserGoals() {

    String firebaseUid = authContext.getFirebaseUid();

    List<UserGoalDto> goals = userGoalService.getAllUserGoalsByFirebaseUid(firebaseUid);

    return new ResponseEntity<>(goals, HttpStatus.OK);
  }

  /**
   * Updates an existing financial goal for the authenticated user.
   * Replaces the current goal details with the provided DTO data.
   *
   * @param goalId      the unique identifier of the goal to update.
   * @param userGoalDto the data transfer object containing the new goal attributes.
   * @return the updated goal details wrapped in a ResponseEntity.
   */
  @PutMapping("/updateGoal/{goalId}")
  public ResponseEntity<UserGoalDto> updateGoal(
          @PathVariable Long goalId,
          @RequestBody UserGoalDto userGoalDto
  ) {
    String firebaseUid = authContext.getFirebaseUid();
    UserGoalDto updatedGoal =
            userGoalService.updateGoal(firebaseUid, goalId, userGoalDto);
    return ResponseEntity.ok(updatedGoal);
  }

  /**
   * Removes a specific financial goal from the authenticated user's account.
   * Validates ownership before deletion and returns the deleted entity.
   *
   * @param goalId the unique identifier of the goal to be removed.
   * @return the details of the deleted goal wrapped in a ResponseEntity.
   */
  @DeleteMapping("/deleteGoal/{goalId}")
  public ResponseEntity<UserGoalDto> deleteGoal(
          @PathVariable Long goalId
  ) {
    String firebaseUid = authContext.getFirebaseUid();
    UserGoalDto deletedGoal =
            userGoalService.deleteGoal(firebaseUid, goalId);
    return ResponseEntity.ok(deletedGoal);
  }

}
