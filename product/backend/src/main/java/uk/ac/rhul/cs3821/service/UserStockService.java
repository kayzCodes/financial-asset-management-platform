package uk.ac.rhul.cs3821.service;

import java.util.List;
import uk.ac.rhul.cs3821.dto.StockDetailsDto;
import uk.ac.rhul.cs3821.dto.StockTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserStockDto;

/**
 * Service interface for managing user stock operations.
 */
public interface UserStockService {

  /**
   * Adds a new stock holding for the specified user.
   *
   * @param firebaseUid  the firebase UID of the user
   * @param userStockDto the stock data to add
   * @return the saved stock data
   */
  UserStockDto addStock(
          String firebaseUid,
          UserStockDto userStockDto,
          StockTransactionRequestDto initialBuy
  );

  /**
   * Retrieves all stocks belonging to a specific user.
   *
   * @param firebaseUid the firebase UID of the user
   * @return a list of a UserStockDto objects
   */
  List<UserStockDto> getAllUserStocksByFirebaseUid(String firebaseUid);

  /**
   * Processes a buy transaction to increase an existing stock position.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holderId    the ID of the stock holding to update.
   * @return the updated {@link UserStockDto} with recalculated averages.
   */
  UserStockDto updateStockBuy(String firebaseUid, Long holderId,
                              StockTransactionRequestDto request);

  /**
   * Processes a sell transaction for an existing stock position.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @return the updated or deleted {@link UserStockDto}.
   */
  UserStockDto updateStockSell(
          String firebaseUid,
          Long holdingId,
          StockTransactionRequestDto request
  );

  /**
   * Permanently removes a stock holding after verifying user ownership.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holdingId   the ID of the stock holding to be deleted.
   * @return the {@link UserStockDto} representation of the removed holding.
   */
  UserStockDto deleteStock(String firebaseUid, Long holdingId);

  /**
   * Aggregates ownership details and market data for a specific stock holding.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holdingId   the ID of the stock holding.
   * @return a {@link StockDetailsDto} containing metrics and performance analysis.
   */
  StockDetailsDto getStockDetail(String firebaseUid, Long holdingId);
}
