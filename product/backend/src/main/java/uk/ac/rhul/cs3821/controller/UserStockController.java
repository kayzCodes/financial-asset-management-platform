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
import uk.ac.rhul.cs3821.dto.AddStockRequestDto;
import uk.ac.rhul.cs3821.dto.StockDetailsDto;
import uk.ac.rhul.cs3821.dto.StockTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserStockDto;
import uk.ac.rhul.cs3821.service.UserStockService;

/**
 * Controller class for handling users' stock-related requests.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/userStock")
public class UserStockController {

  private final UserStockService userStockService;
  private final AuthContext authContext;

  /**
   * Adds a new stock record for the authenticated user.
   */
  @PostMapping("/addStock")
  public ResponseEntity<UserStockDto> addStock(
          @RequestBody AddStockRequestDto request
  ) {

    String firebaseUid = authContext.getFirebaseUid();

    if (request == null || request.holding() == null) {
      throw new RuntimeException("Holding is required.");
    }

    if (request.initialBuy() == null) {
      throw new RuntimeException("Initial buy is required.");
    }

    UserStockDto savedStock = userStockService.addStock(
            firebaseUid,
            request.holding(),
            request.initialBuy()
    );

    return new ResponseEntity<>(savedStock, HttpStatus.CREATED);
  }

  /**
   * Retrieves all stock holdings for the authenticated user.
   */
  @GetMapping("/getStocks")
  public ResponseEntity<List<UserStockDto>> getStocks() {

    String firebaseUid = authContext.getFirebaseUid();
    List<UserStockDto> stocks = userStockService.getAllUserStocksByFirebaseUid(firebaseUid);

    return new ResponseEntity<>(stocks, HttpStatus.OK);
  }

  /**
   * Removes a specific stock holding from the authenticated user's portfolio.
   * Validates ownership and returns the details of the removed asset.
   *
   * @param holdingId the unique identifier of the stock holding to delete.
   * @return the details of the deleted stock holding wrapped in a ResponseEntity.
   */
  @DeleteMapping("/deleteStock/{holdingId}")
  public ResponseEntity<UserStockDto> deleteStock(
          @PathVariable Long holdingId
  ) {
    String firebaseUid = authContext.getFirebaseUid();
    UserStockDto deletedStock = userStockService.deleteStock(firebaseUid, holdingId);
    return ResponseEntity.ok(deletedStock);
  }

  /**
   * Retrieves real-time market data and holding details for a specific stock position.
   * Validates the user's ownership of the holding before returning data.
   *
   * @param holdingId the unique identifier of the stock holding.
   * @return the detailed data transfer object containing live metrics.
   */
  @GetMapping("/getLiveStockDetails/{holdingId}")
  public ResponseEntity<StockDetailsDto> getStockDetail(@PathVariable Long holdingId) {
    String firebaseUid = authContext.getFirebaseUid();
    return ResponseEntity.ok(userStockService.getStockDetail(firebaseUid, holdingId));
  }

  /**
   * Updates a stock holding to reflect a new purchase transaction.
   * Recalculates the average cost basis and total quantity held.
   *
   * @param holdingId the unique identifier of the stock holding.
   * @return the updated stock holding details wrapped in a ResponseEntity.
   */
  @PutMapping("/updateStockBuy/{holdingId}")
  public ResponseEntity<UserStockDto> buyStock(
          @PathVariable Long holdingId,
          @RequestBody StockTransactionRequestDto request
  ) {

    String firebaseUid = authContext.getFirebaseUid();

    UserStockDto updated =
            userStockService.updateStockBuy(
                    firebaseUid,
                    holdingId,
                    request
            );

    return ResponseEntity.ok(updated);
  }

  /**
   * Updates a stock holding to reflect a sale of shares.
   * Deducts the specified quantity from the user's position and returns the updated state.
   *
   * @param holdingId the unique identifier of the stock holding to be updated.
   * @return the updated stock holding details wrapped in a ResponseEntity.
   */
  @PutMapping("/updateStockSell/{holdingId}")
  public ResponseEntity<UserStockDto> sellStock(
          @PathVariable Long holdingId,
          @RequestBody StockTransactionRequestDto request
  ) {

    String firebaseUid = authContext.getFirebaseUid();

    UserStockDto updated =
            userStockService.updateStockSell(
                    firebaseUid,
                    holdingId,
                    request
            );

    return ResponseEntity.ok(updated);
  }

}
