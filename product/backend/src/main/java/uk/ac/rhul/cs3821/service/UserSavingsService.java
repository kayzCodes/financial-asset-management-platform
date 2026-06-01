package uk.ac.rhul.cs3821.service;

import java.util.List;
import uk.ac.rhul.cs3821.dto.AddSavingRequestDto;
import uk.ac.rhul.cs3821.dto.UserSavingsDto;

/**
 * The user savings service interface.
 */
public interface UserSavingsService {

  /**
   * Adds a new saving entry for a specific goal belonging to the authenticated user.
   *
   * @param firebaseUid the Firebase UID of the authenticated user
   * @param request     the saving details (goalId and amount)
   * @return the saved entry as UserSavingsDto
   */
  UserSavingsDto addSaving(String firebaseUid, AddSavingRequestDto request);

  /**
   * Retrieves all savings entries for a specific goal belonging to the user.
   *
   * @param firebaseUid the Firebase UID of the user
   * @param goalId      the ID of the goal
   * @return a list of UserSavingsDto objects
   */
  List<UserSavingsDto> getSavingsByGoal(String firebaseUid, Long goalId);

  /**
   * Retrieves all savings entries belonging to the authenticated user.
   *
   * @param firebaseUid the Firebase UID of the user
   * @return a list of UserSavingsDto objects
   */
  List<UserSavingsDto> getAllSavingsByUser(String firebaseUid);

  /**
   * Deletes a specific saving entry after verifying ownership.
   *
   * @param firebaseUid the Firebase UID of the authenticated user
   * @param savingId    the ID of the saving entry
   * @return the deleted saving as UserSavingsDto
   */
  UserSavingsDto deleteSaving(String firebaseUid, Long savingId);
}