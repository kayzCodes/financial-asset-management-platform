package uk.ac.rhul.cs3821.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.dto.AddSavingRequestDto;
import uk.ac.rhul.cs3821.dto.UserSavingsDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserGoal;
import uk.ac.rhul.cs3821.entity.UserSavings;
import uk.ac.rhul.cs3821.mapper.UserSavingsMapper;
import uk.ac.rhul.cs3821.repository.UserGoalRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.repository.UserSavingsRepository;
import uk.ac.rhul.cs3821.service.UserSavingsService;

/**
 * Implementation of UserSavingsService.
 */
@Service
@RequiredArgsConstructor
public class UserSavingsServiceImpl implements UserSavingsService {

  private final UserRepository userRepository;
  private final UserGoalRepository userGoalRepository;
  private final UserSavingsRepository userSavingsRepository;

  /**
   * Adds a new saving entry for a specific goal belonging to the authenticated user.
   *
   * @param firebaseUid the Firebase UID of the authenticated user
   * @param request     the saving details (goalId and amount)
   * @return the saved entry as UserSavingsDto
   */
  @Override
  public UserSavingsDto addSaving(String firebaseUid, AddSavingRequestDto request) {

    // 1 Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2 Validate goal
    UserGoal goal = userGoalRepository.findById(request.goalId())
            .orElseThrow(() -> new RuntimeException("Goal not found"));

    // 3 Ownership check (CRITICAL)
    if (!goal.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Goal does not belong to user");
    }

    // 4 Validate amount
    if (request.amount() == null
            || request.amount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("Amount must be greater than zero");
    }

    // 5 Create saving
    UserSavings saving = UserSavings.builder()
            .user(user)
            .goal(goal)
            .amount(request.amount())
            .createdAt(LocalDateTime.now())
            .build();

    // FIX: capture the saved entity (this is what contains the ID)
    UserSavings savedSaving = userSavingsRepository.save(saving);

    // 6 Update goal current amount
    goal.setCurrentAmount(
            goal.getCurrentAmount().add(request.amount())
    );
    userGoalRepository.save(goal);

    // FIX: return the SAVED entity, not the original
    return UserSavingsMapper.mapToDto(savedSaving);
  }

  /**
   * Retrieves all savings entries for a specific goal belonging to the user.
   *
   * @param firebaseUid the Firebase UID of the user
   * @param goalId      the ID of the goal
   * @return a list of UserSavingsDto objects
   */
  @Override
  public List<UserSavingsDto> getSavingsByGoal(String firebaseUid, Long goalId) {

    // 1 Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2 Validate goal
    UserGoal goal = userGoalRepository.findById(goalId)
            .orElseThrow(() -> new RuntimeException("Goal not found"));

    // 3 Ownership check
    if (!goal.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Goal does not belong to user");
    }

    return userSavingsRepository.findByGoalId(goalId)
            .stream()
            .map(UserSavingsMapper::mapToDto)
            .toList();
  }

  /**
   * Retrieves all savings entries belonging to the authenticated user.
   *
   * @param firebaseUid the Firebase UID of the user
   * @return a list of UserSavingsDto objects
   */
  @Override
  public List<UserSavingsDto> getAllSavingsByUser(String firebaseUid) {

    // 1 Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return userSavingsRepository.findByUserId(user.getId())
            .stream()
            .map(UserSavingsMapper::mapToDto)
            .toList();
  }

  /**
   * Deletes a specific saving entry after verifying ownership.
   *
   * @param firebaseUid the Firebase UID of the authenticated user
   * @param savingId    the ID of the saving entry
   * @return the deleted saving as UserSavingsDto
   */
  @Override
  public UserSavingsDto deleteSaving(String firebaseUid, Long savingId) {

    // 1 Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2 Fetch saving
    UserSavings saving = userSavingsRepository.findById(savingId)
            .orElseThrow(() -> new RuntimeException("Saving not found"));

    // 3 Ownership check
    if (!saving.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Saving does not belong to user");
    }

    // 4 Update goal (reduce amount)
    UserGoal goal = saving.getGoal();
    goal.setCurrentAmount(
            goal.getCurrentAmount().subtract(saving.getAmount())
    );
    userGoalRepository.save(goal);

    // 5 Delete saving
    userSavingsRepository.delete(saving);

    return UserSavingsMapper.mapToDto(saving);
  }
}