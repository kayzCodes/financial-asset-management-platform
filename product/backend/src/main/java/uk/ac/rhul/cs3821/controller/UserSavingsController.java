package uk.ac.rhul.cs3821.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.AddSavingRequestDto;
import uk.ac.rhul.cs3821.dto.UserSavingsDto;
import uk.ac.rhul.cs3821.service.UserSavingsService;

/**
 * REST controller exposing endpoints for managing user savings.
 */
@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
public class UserSavingsController {

  private final UserSavingsService userSavingsService;
  private final AuthContext authContext;


  /**
   * Adds a new saving entry for a goal.
   *
   * @param request the saving request (goalId + amount)
   * @return the saved entry
   */
  @PostMapping("/addSaving")
  public ResponseEntity<UserSavingsDto> addSaving(
          @RequestBody AddSavingRequestDto request
  ) {
    String firebaseUid = authContext.getFirebaseUid();

    return ResponseEntity.ok(
            userSavingsService.addSaving(firebaseUid, request)
    );
  }

  /**
   * Retrieves all savings for a specific goal.
   *
   * @param goalId the goal ID
   * @return list of savings
   */
  @GetMapping("/getSavingsByGoal/{goalId}")
  public ResponseEntity<List<UserSavingsDto>> getSavingsByGoal(
          @PathVariable Long goalId
  ) {
    String firebaseUid = authContext.getFirebaseUid();

    return ResponseEntity.ok(
            userSavingsService.getSavingsByGoal(firebaseUid, goalId)
    );
  }

  /**
   * Retrieves all savings for the authenticated user.
   *
   * @return list of savings
   */
  @GetMapping("/getAllSavings")
  public ResponseEntity<List<UserSavingsDto>> getAllSavings() {
    String firebaseUid = authContext.getFirebaseUid();

    return ResponseEntity.ok(
            userSavingsService.getAllSavingsByUser(firebaseUid)
    );
  }

  /**
   * Deletes a specific saving entry.
   *
   * @param savingId the ID of the saving
   * @return deleted saving
   */
  @DeleteMapping("deleteSavingBySavingId/{savingId}")
  public ResponseEntity<UserSavingsDto> deleteSaving(
          @PathVariable Long savingId
  ) {
    String firebaseUid = authContext.getFirebaseUid();

    return ResponseEntity.ok(
            userSavingsService.deleteSaving(firebaseUid, savingId)
    );
  }
}