package uk.ac.rhul.cs3821.controller;

import java.util.List;
import lombok.AllArgsConstructor;
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
import uk.ac.rhul.cs3821.dto.AddCryptoRequestDto;
import uk.ac.rhul.cs3821.dto.CryptoDetailsDto;
import uk.ac.rhul.cs3821.dto.CryptoTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserCryptoDto;
import uk.ac.rhul.cs3821.service.UserCryptoService;

/**
 * Controller class for handling users' crypto-related requests.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/userCrypto")
public class UserCryptoController {

  private final UserCryptoService userCryptoService;
  private final AuthContext authContext;

  /**
   * Adds a new crypto record for the authenticated user.
   *
   * @return the saved crypto data with HTTP status 201 (Created)
   */
  @PostMapping("/addCrypto")
  public ResponseEntity<UserCryptoDto> addCrypto(
          @RequestBody AddCryptoRequestDto request
  ) {

    String firebaseUid = authContext.getFirebaseUid();

    if (request == null || request.holding() == null) {
      throw new RuntimeException("Holding is required.");
    }

    if (request.initialBuy() == null) {
      throw new RuntimeException("Initial buy is required.");
    }

    UserCryptoDto savedCrypto = userCryptoService.addCrypto(
            firebaseUid,
            request.holding(),
            request.initialBuy()
    );

    return new ResponseEntity<>(savedCrypto, HttpStatus.CREATED);
  }

  /**
   * Retrieves all crypto holdings for the authenticated user.
   *
   * @return list of crypto holdings with HTTP status 200 (OK)
   */
  @GetMapping("/getCryptos")
  public ResponseEntity<List<UserCryptoDto>> getCryptos() {

    String firebaseUid = authContext.getFirebaseUid();
    List<UserCryptoDto> cryptos = userCryptoService.getAllUserCryptoByFirebaseUid(firebaseUid);

    return new ResponseEntity<>(cryptos, HttpStatus.OK);
  }

  /**
   * Removes a specific cryptocurrency holding from the authenticated user's portfolio.
   * Ensures the user owns the holding before attempting deletion.
   *
   * @param holdingId the unique identifier of the cryptocurrency holding to delete.
   * @return the details of the deleted holding wrapped in a ResponseEntity.
   */
  @DeleteMapping("/deleteCrypto/{holdingId}")
  public ResponseEntity<UserCryptoDto> deleteCrypto(
          @PathVariable Long holdingId
  ) {
    String firebaseUid = authContext.getFirebaseUid();
    UserCryptoDto deletedCrypto =
            userCryptoService.deleteCrypto(firebaseUid, holdingId);
    return ResponseEntity.ok(deletedCrypto);
  }

  /**
   * Retrieves real-time details for a specific cryptocurrency holding.
   * Validates ownership against the authenticated user's context.
   *
   * @param holdingId the unique identifier of the crypto holding.
   * @return the detailed data transfer object for the requested holding.
   */
  @GetMapping("/getLiveCryptoDetails/{holdingId}")
  public ResponseEntity<CryptoDetailsDto> getCryptoDetail(@PathVariable Long holdingId) {
    String firebaseUid = authContext.getFirebaseUid();
    return ResponseEntity.ok(userCryptoService.getCryptoDetail(firebaseUid, holdingId));
  }

  /**
   * Updates the purchase metrics (average price and quantity) for a cryptocurrency holding.
   * modifying the user's portfolio state.
   *
   * @param holdingId the unique identifier of the crypto holding.
   * @return the updated holding details wrapped in a ResponseEntity.
   */
  @PutMapping("/updateCryptoBuy/{holdingId}")
  public ResponseEntity<UserCryptoDto> buyCrypto(
          @PathVariable Long holdingId,
          @RequestBody CryptoTransactionRequestDto request
  ) {

    String firebaseUid = authContext.getFirebaseUid();

    UserCryptoDto updated =
            userCryptoService.updateCryptoBuy(
                    firebaseUid,
                    holdingId,
                    request
            );

    return ResponseEntity.ok(updated);
  }

  /**
   * Updates a cryptocurrency holding to reflect a sell transaction.
   * Reduces the held quantity while maintaining the existing cost basis.
   *
   * @param holdingId the unique identifier of the crypto holding.
   * @return the updated holding details after the sale.
   */
  @PutMapping("/updateCryptoSell/{holdingId}")
  public ResponseEntity<UserCryptoDto> sellCrypto(
          @PathVariable Long holdingId,
          @RequestBody CryptoTransactionRequestDto request
  ) {

    String firebaseUid = authContext.getFirebaseUid();

    UserCryptoDto updated =
            userCryptoService.updateCryptoSell(
                    firebaseUid,
                    holdingId,
                    request
            );

    return ResponseEntity.ok(updated);
  }
}
