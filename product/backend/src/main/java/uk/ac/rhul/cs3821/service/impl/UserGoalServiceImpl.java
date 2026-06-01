package uk.ac.rhul.cs3821.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserGoalDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserGoal;
import uk.ac.rhul.cs3821.mapper.UserGoalMapper;
import uk.ac.rhul.cs3821.mapper.UserMapper;
import uk.ac.rhul.cs3821.repository.UserGoalRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.service.UserGoalService;
import uk.ac.rhul.cs3821.service.UserService;

/**
 * Service implementation for managing user goals.
 */
@Service
@RequiredArgsConstructor
public class UserGoalServiceImpl implements UserGoalService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final UserGoalRepository userGoalRepository;

  /**
   * Creates and persists a new financial goal for a specific user.
   * * Verifies user existence via Firebase UID, maps the goal DTO to a persistent
   * entity, establishes the user relationship, and returns the saved goal.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param goalDto     the data transfer object containing goal details.
   * @return the created goal mapped back to a {@link UserGoalDto}.
   * @throws RuntimeException if no user is found for the provided UID.
   */
  @Override
  public UserGoalDto createGoalForUser(String firebaseUid, UserGoalDto goalDto) {

    UserDto userDto = userService.getUserByFirebaseUid(firebaseUid);

    if (userDto == null) {
      throw new RuntimeException("User not found for Firebase UID: " + firebaseUid);
    }

    User userEntity = UserMapper.mapToUser(userDto);

    UserGoal goal = UserGoalMapper.mapToUserGoal(goalDto);

    goal.setUser(userEntity);

    UserGoal savedGoal = userGoalRepository.save(goal);

    return UserGoalMapper.mapToUserGoalDto(savedGoal);
  }

  /**
   * Retrieves all financial goals associated with a specific user.
   * * Verifies the user via Firebase UID, queries the repository for all goals
   * linked to the resolved user ID, and transforms the resulting entities into DTOs.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @return a list of {@link UserGoalDto} objects for the user.
   * @throws RuntimeException if the user is not found.
   */
  @Override
  public List<UserGoalDto> getAllUserGoalsByFirebaseUid(String firebaseUid) {

    UserDto userDto = userService.getUserByFirebaseUid(firebaseUid);
    if (userDto == null) {
      throw new RuntimeException("User not found for Firebase UID: " + firebaseUid);
    }

    Long userId = userDto.getId();

    List<UserGoal> goals = userGoalRepository.findByUserId(userId);

    return goals.stream()
            .map(UserGoalMapper::mapToUserGoalDto)
            .collect(Collectors.toList());
  }

  /**
   * Updates an existing financial goal after verifying user ownership.
   * * Validates the user and target goal, ensuring the goal belongs to the requester.
   * Updates the title, description, target amount, current progress, and deadline
   * before persisting the changes.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holderId    the ID of the specific goal to update.
   * @param userGoalDto the DTO containing the updated goal details.
   * @return the updated {@link UserGoalDto}.
   * @throws RuntimeException if the user or goal is not found, or if ownership validation fails.
   */
  @Override
  public UserGoalDto updateGoal(String firebaseUid, Long holderId, UserGoalDto userGoalDto) {

    // 1. Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException(
                    "User not found for Firebase UID: " + firebaseUid));

    // 2. Fetch existing goal
    UserGoal existingGoal = userGoalRepository.findById(holderId)
            .orElseThrow(() -> new RuntimeException(
                    "Goal not found for ID: " + holderId));

    // 3. Ownership check
    if (!existingGoal.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Goal does not belong to the authenticated user");
    }

    // 4. Update fields from DTO (exclude id + user)
    existingGoal.setGoalTitle(userGoalDto.getGoalTitle());
    existingGoal.setDescription(userGoalDto.getDescription());
    existingGoal.setTargetAmount(userGoalDto.getTargetAmount());
    existingGoal.setCurrentAmount(userGoalDto.getCurrentAmount());
    existingGoal.setDeadline(userGoalDto.getDeadline());

    // 5. Persist and return updated DTO
    UserGoal updatedGoal = userGoalRepository.save(existingGoal);
    return UserGoalMapper.mapToUserGoalDto(updatedGoal);
  }

  /**
   * Deletes a specific financial goal after verifying user ownership.
   * Validates that the goal exists and belongs to the authenticated user before
   * removing it from the database. Returns the deleted goal data as a DTO.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param id          the primary key of the goal to be deleted.
   * @return the {@link UserGoalDto} representation of the removed goal.
   * @throws RuntimeException if the user or goal is not found, or ownership is invalid.
   */
  @Override
  public UserGoalDto deleteGoal(String firebaseUid, Long id) {

    // 1. Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException(
                    "User not found for Firebase UID: " + firebaseUid));

    // 2. Fetch goal
    UserGoal goal = userGoalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(
                    "Goal not found for ID: " + id));

    // 3. Ownership check
    if (!goal.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Goal does not belong to the authenticated user");
    }

    // 4. Delete + return deleted DTO
    userGoalRepository.delete(goal);
    return UserGoalMapper.mapToUserGoalDto(goal);
  }


}
