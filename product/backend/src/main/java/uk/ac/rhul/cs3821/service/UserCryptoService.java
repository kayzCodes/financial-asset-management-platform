package uk.ac.rhul.cs3821.service;


import java.util.List;
import uk.ac.rhul.cs3821.dto.CryptoDetailsDto;
import uk.ac.rhul.cs3821.dto.CryptoTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserCryptoDto;

/**
 * This is the user crypto service.
 */
public interface UserCryptoService {

  /**
   * Adds a new cryptocurrency holding for the specified user.
   *
   * @param firebaseUid   the firebase UID of the user
   * @param userCryptoDto the crypto data to add
   * @return the saved crypto data
   */
  UserCryptoDto addCrypto(
          String firebaseUid,
          UserCryptoDto userCryptoDto,
          CryptoTransactionRequestDto initialBuy
  );

  /**
   * Retrieves all cryptocurrency belonging to a specific user.
   *
   * @param firebaseUid the firebase UID of the user
   * @return a list of a UserCryptoDto objects
   */
  List<UserCryptoDto> getAllUserCryptoByFirebaseUid(String firebaseUid);

  /**
   * Processes a sell transaction for a specific cryptocurrency holding.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holdingId   the ID of the crypto holding to update.
   * @return the updated or deleted {@link UserCryptoDto}.
   */
  UserCryptoDto updateCryptoSell(
          String firebaseUid,
          Long holdingId,
          CryptoTransactionRequestDto request
  );

  /**
   * Processes a buy transaction to increase an existing cryptocurrency position.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holdingId   the ID of the crypto holding to update.
   * @return the updated {@link UserCryptoDto} with recalculated averages.
   */
  UserCryptoDto updateCryptoBuy(
          String firebaseUid,
          Long holdingId,
          CryptoTransactionRequestDto request
  );

  /**
   * Permanently removes a cryptocurrency holding after verifying user ownership.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holdingId   the ID of the crypto holding to be deleted.
   * @return the {@link UserCryptoDto} representation of the removed holding.
   */
  UserCryptoDto deleteCrypto(String firebaseUid, Long holdingId);

  /**
   * Aggregates ownership details and market data for a specific cryptocurrency holding.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holdingId   the ID of the crypto holding.
   * @return a {@link CryptoDetailsDto} containing metrics and performance analysis.
   */
  CryptoDetailsDto getCryptoDetail(String firebaseUid, Long holdingId);

}
