package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.dto.ChartPointDto;
import uk.ac.rhul.cs3821.dto.StockDetailsDto;
import uk.ac.rhul.cs3821.dto.StockFundamentalsDto;
import uk.ac.rhul.cs3821.dto.StockTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserStockDto;
import uk.ac.rhul.cs3821.entity.AssetTransaction;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.enums.AssetType;
import uk.ac.rhul.cs3821.enums.TransactionType;
import uk.ac.rhul.cs3821.mapper.UserStockMapper;
import uk.ac.rhul.cs3821.repository.AssetTransactionRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.repository.UserStockRepository;
import uk.ac.rhul.cs3821.service.FxRateService;
import uk.ac.rhul.cs3821.service.StockFundamentalService;
import uk.ac.rhul.cs3821.service.UserStockService;

/**
 * Service implementation for managing user stock operations.
 */
@Service
@AllArgsConstructor
public class UserStockServiceImpl implements UserStockService {

  private final UserRepository userRepository;
  private final StockFundamentalService stockFundamentalService;
  private final UserStockRepository userStockRepository;
  private final FxRateService fxRateService;
  private final AssetTransactionRepository assetTransactionRepository;
  private final AlphaVantageDailySeriesProvider alphaVantageDailySeriesProvider;
  private final LivePriceServiceImpl livePriceServiceImpl;

  /**
   * Populates a map with stock financial metrics and retrieves the asset description.
   * Transfers key statistics from a fundamentals DTO into the provided map and
   * returns the company description. Returns null if the fundamentals DTO is missing.
   *
   * @param fundamentals the DTO containing stock financial data.
   * @param keyStats     the map where specific financial metrics will be stored.
   * @return the stock description, or null if fundamentals are unavailable.
   */
  private String applyFundamentals(
          StockFundamentalsDto fundamentals,
          Map<String, String> keyStats
  ) {
    if (fundamentals == null) {
      return null;
    }

    keyStats.put("MarketCapitalization", fundamentals.getMarketCap());
    keyStats.put("PERatio", fundamentals.getPeRatio());
    keyStats.put("EPS", fundamentals.getEps());
    keyStats.put("Sector", fundamentals.getSector());
    keyStats.put("Industry", fundamentals.getIndustry());

    return fundamentals.getDescription();
  }

  /**
   * Creates a new stock holding with an initial buy transaction.
   *
   * @param firebaseUid  the authenticated user identifier
   * @param userStockDto stock holding details
   * @param initialBuy   initial buy transaction request
   * @return created {@link UserStockDto}
   */
  @Override
  public UserStockDto addStock(
          String firebaseUid,
          UserStockDto userStockDto,
          StockTransactionRequestDto initialBuy
  ) {

    final User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException(
                    "User not found for Firebase UID: " + firebaseUid));

    if (initialBuy == null) {
      throw new RuntimeException("Initial buy is required");
    }

    BigDecimal quantity = initialBuy.getQuantity();
    BigDecimal pricePerUnit = initialBuy.getPricePerUnit();

    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("Buy quantity must be positive");
    }

    if (pricePerUnit == null || pricePerUnit.compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("Price per unit must be positive");
    }

    LocalDateTime occurredAt = initialBuy.getOccurredAt() != null
            ? initialBuy.getOccurredAt()
            : LocalDateTime.now();

    if (occurredAt.isAfter(LocalDateTime.now())) {
      throw new RuntimeException("Transaction date cannot be in the future");
    }

    UserStock userStock = UserStockMapper.mapToUserStock(userStockDto);
    userStock.setUser(user);

    // Snapshot fields must match the transaction (not whatever came in the holding DTO).
    userStock.setQuantity(quantity);
    userStock.setAveragePurchasePrice(pricePerUnit);
    userStock.setLastTransactionAt(occurredAt);

    UserStock savedUserStock = userStockRepository.save(userStock);

    BigDecimal fxRate =
            fxRateService.getFxToGbp(savedUserStock.getCurrency(), occurredAt);

    AssetTransaction transaction = new AssetTransaction();
    transaction.setUser(user);
    transaction.setStockHolding(savedUserStock);
    transaction.setAssetType(AssetType.STOCK);
    transaction.setTransactionType(TransactionType.BUY);
    transaction.setQuantity(quantity);
    transaction.setPricePerUnit(pricePerUnit);
    transaction.setCurrency(savedUserStock.getCurrency());
    transaction.setFxRateToGbp(fxRate);
    transaction.setOccurredAt(occurredAt);

    assetTransactionRepository.save(transaction);

    return UserStockMapper.mapToUserStockDto(savedUserStock);
  }

  /**
   * Retrieves all stock holdings associated with a specific user.
   * * Locates the user via Firebase UID to obtain their internal ID, then queries
   * the repository for all related stock records and transforms them into DTOs.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @return a list of {@link UserStockDto} objects representing the user's holdings.
   * @throws RuntimeException if no user is found for the provided UID.
   */
  @Override
  public List<UserStockDto> getAllUserStocksByFirebaseUid(String firebaseUid) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found for Firebase UID: "
                    + firebaseUid));

    Long userId = user.getId();

    List<UserStock> stocks = userStockRepository.findByUserIdAndIsDeletedFalse(userId);

    return stocks.stream()
            .map(UserStockMapper::mapToUserStockDto)
            .collect(Collectors.toList());
  }

  /**
   * Deletes a specific stock holding after verifying user ownership.
   * * Ensures the holding exists and belongs to the authenticated user before removal.
   * Returns the data of the deleted holding as a DTO.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holdingId   the ID of the stock holding to be deleted.
   * @return the deleted {@link UserStockDto}.
   * @throws RuntimeException if the user or stock is not found, or ownership is invalid.
   */
  @Override
  public UserStockDto deleteStock(String firebaseUid, Long holdingId) {

    // 1. Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException(
                    "User not found for Firebase UID: " + firebaseUid));

    // 2. Fetch stock
    UserStock stock = userStockRepository.findById(holdingId)
            .orElseThrow(() -> new RuntimeException(
                    "Stock not found for ID: " + holdingId));

    // 3. Ownership check
    if (!stock.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Stock does not belong to the authenticated user");
    }

    BigDecimal quantity = stock.getQuantity();

    // 4. If shares remain → auto close position
    if (quantity.compareTo(BigDecimal.ZERO) > 0) {

      BigDecimal marketPrice =
              livePriceServiceImpl.getCurrentStockPrice((stock.getTickerSymbol()));

      if (marketPrice == null) {
        throw new RuntimeException("Unable to fetch current market price");
      }

      LocalDateTime occurredAt = LocalDateTime.now();

      BigDecimal fxRate =
              fxRateService.getFxToGbp(stock.getCurrency(), occurredAt);

      AssetTransaction transaction = new AssetTransaction();
      transaction.setUser(user);
      transaction.setStockHolding(stock);
      transaction.setAssetType(AssetType.STOCK);
      transaction.setTransactionType(TransactionType.SELL);
      transaction.setQuantity(quantity);
      transaction.setPricePerUnit(marketPrice);
      transaction.setCurrency(stock.getCurrency());
      transaction.setFxRateToGbp(fxRate);
      transaction.setOccurredAt(occurredAt);

      assetTransactionRepository.save(transaction);

      stock.setQuantity(BigDecimal.ZERO);
      stock.setIsDeleted(true);
      stock.setLastTransactionAt(occurredAt);

      UserStock saved = userStockRepository.save(stock);

      return UserStockMapper.mapToUserStockDto(saved);
    }

    // 5. If already empty → just hide
    stock.setIsDeleted(true);

    UserStock saved = userStockRepository.save(stock);

    return UserStockMapper.mapToUserStockDto(saved);
  }

  /**
   * Builds complete stock details including fundamentals, pricing history,
   * valuation, and performance metrics for the authenticated user's holding.
   *
   * @param firebaseUid the authenticated user identifier
   * @param holdingId   the stock holding identifier
   * @return populated {@link StockDetailsDto} with asset metadata and analytics
   * @throws RuntimeException if the user, holding, or ownership validation fails
   */
  @Override
  public StockDetailsDto getStockDetail(
          String firebaseUid,
          Long holdingId) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() ->
                    new RuntimeException(
                            "User not found for Firebase UID: "
                                    + firebaseUid));

    UserStock holding = userStockRepository.findById(holdingId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Stock not found for ID: "
                                    + holdingId));

    if (!holding.getUser().getId().equals(user.getId())) {
      throw new RuntimeException(
              "Stock does not belong to the authenticated user");
    }

    String symbol = holding.getTickerSymbol();

    // --- Fundamentals ---
    StockFundamentalsDto fundamentals =
            stockFundamentalService.getStockFundamentals(symbol);

    Map<String, String> keyStats = new HashMap<>();
    String companyOverview = applyFundamentals(fundamentals, keyStats);

    // --- Daily price series (centralised provider) ---
    Map<LocalDate, BigDecimal> series =
            alphaVantageDailySeriesProvider.getStockDailyCloseSeries(symbol);

    BigDecimal latestClose = null;
    BigDecimal percentageChange = null;
    BigDecimal currentValue = null;
    BigDecimal priceDifference = null;
    BigDecimal percentageChangeFromAveragePrice = null;

    List<ChartPointDto> chartData = List.of();

    if (series != null && !series.isEmpty()) {

      List<Map.Entry<LocalDate, BigDecimal>> entries =
              new ArrayList<>(series.entrySet());

      entries.sort(Map.Entry.comparingByKey());

      latestClose = entries.get(entries.size() - 1).getValue();

      if (entries.size() > 1) {

        BigDecimal prevClose =
                entries.get(entries.size() - 2).getValue();

        if (prevClose.compareTo(BigDecimal.ZERO) != 0) {
          percentageChange = latestClose
                  .subtract(prevClose)
                  .divide(prevClose, 6, RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(100));
        }
      }

      if (latestClose != null) {

        currentValue =
                latestClose.multiply(holding.getQuantity());

        BigDecimal totalCost =
                holding.getAveragePurchasePrice()
                        .multiply(holding.getQuantity());

        priceDifference =
                currentValue.subtract(totalCost);

        if (holding.getAveragePurchasePrice()
                .compareTo(BigDecimal.ZERO) > 0) {

          percentageChangeFromAveragePrice =
                  latestClose
                          .subtract(holding.getAveragePurchasePrice())
                          .divide(
                                  holding.getAveragePurchasePrice(),
                                  6,
                                  RoundingMode.HALF_UP
                          )
                          .multiply(BigDecimal.valueOf(100));
        }
      }

      chartData = entries.stream()
              .map(e -> new ChartPointDto(
                      e.getKey().toString(),
                      e.getValue()
              ))
              .toList();
    }

    return new StockDetailsDto(
            holding.getTickerSymbol(),
            holding.getCompanyName(),
            holding.getQuantity(),
            holding.getAveragePurchasePrice(),
            latestClose,
            percentageChange,
            currentValue,
            priceDifference,
            percentageChangeFromAveragePrice,
            chartData,
            keyStats,
            companyOverview
    );
  }

  /**
   * Records a buy transaction for a stock holding and updates its snapshot state.
   *
   * @param firebaseUid the authenticated user identifier
   * @param holdingId   stock holding identifier
   * @param request     buy transaction details
   * @return updated {@link UserStockDto}
   */
  @Override
  public UserStockDto updateStockBuy(
          String firebaseUid,
          Long holdingId,
          StockTransactionRequestDto request
  ) {

    // 1 Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2 Fetch stock
    UserStock stock = userStockRepository.findById(holdingId)
            .orElseThrow(() -> new RuntimeException("Stock not found"));

    // 3 Ownership check
    if (!stock.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Stock does not belong to user");
    }

    BigDecimal quantity = request.getQuantity();
    BigDecimal pricePerUnit = request.getPricePerUnit();

    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("Buy quantity must be positive");
    }

    if (pricePerUnit == null || pricePerUnit.compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("Price per unit must be positive");
    }

    // 4 Resolve occurredAt
    LocalDateTime occurredAt = request.getOccurredAt() != null
            ? request.getOccurredAt()
            : LocalDateTime.now();

    if (occurredAt.isAfter(LocalDateTime.now())) {
      throw new RuntimeException("Transaction date cannot be in the future");
    }

    // 5 Fetch FX
    BigDecimal fxRate =
            fxRateService.getFxToGbp(stock.getCurrency(), occurredAt);

    // 6 Create AssetTransaction
    AssetTransaction transaction = new AssetTransaction();
    transaction.setUser(user);
    transaction.setStockHolding(stock);
    transaction.setAssetType(AssetType.STOCK);
    transaction.setTransactionType(TransactionType.BUY);
    transaction.setQuantity(quantity);
    transaction.setPricePerUnit(pricePerUnit);
    transaction.setCurrency(stock.getCurrency());
    transaction.setFxRateToGbp(fxRate);
    transaction.setOccurredAt(occurredAt);

    assetTransactionRepository.save(transaction);

    // 7 Existing snapshot logic (unchanged)
    BigDecimal oldQty = stock.getQuantity();
    BigDecimal oldAvg = stock.getAveragePurchasePrice();

    BigDecimal oldTotalCost = oldAvg.multiply(oldQty);
    BigDecimal newBuyCost = pricePerUnit.multiply(quantity);

    BigDecimal newTotalQty = oldQty.add(quantity);

    BigDecimal newAvgPrice = oldTotalCost.add(newBuyCost)
            .divide(newTotalQty, 6, RoundingMode.HALF_UP);

    stock.setQuantity(newTotalQty);
    stock.setAveragePurchasePrice(newAvgPrice);
    stock.setLastTransactionAt(occurredAt);

    UserStock saved = userStockRepository.save(stock);

    return UserStockMapper.mapToUserStockDto(saved);
  }

  /**
   * Records a sell transaction for a stock holding and updates its snapshot state.
   *
   * @param firebaseUid the authenticated user identifier
   * @param holdingId   stock holding identifier
   * @param request     sell transaction details
   * @return updated {@link UserStockDto}
   */
  @Override
  public UserStockDto updateStockSell(
          String firebaseUid,
          Long holdingId,
          StockTransactionRequestDto request
  ) {

    // 1 Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2 Fetch stock
    UserStock stock = userStockRepository.findById(holdingId)
            .orElseThrow(() -> new RuntimeException("Stock not found"));

    // 3 Ownership check
    if (!stock.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Stock does not belong to user");
    }

    BigDecimal quantity = request.getQuantity();

    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("Sell quantity must be positive");
    }

    BigDecimal currentQty = stock.getQuantity();

    if (quantity.compareTo(currentQty) > 0) {
      throw new RuntimeException("Cannot sell more than owned quantity");
    }

    // 4 Resolve occurredAt
    LocalDateTime occurredAt = request.getOccurredAt() != null
            ? request.getOccurredAt()
            : LocalDateTime.now();

    if (occurredAt.isAfter(LocalDateTime.now())) {
      throw new RuntimeException("Transaction date cannot be in the future");
    }

    // 5 Fetch FX
    BigDecimal fxRate =
            fxRateService.getFxToGbp(stock.getCurrency(), occurredAt);

    // 6 Create SELL transaction
    AssetTransaction transaction = new AssetTransaction();
    transaction.setUser(user);
    transaction.setStockHolding(stock);
    transaction.setAssetType(AssetType.STOCK);
    transaction.setTransactionType(TransactionType.SELL);
    transaction.setQuantity(quantity);
    transaction.setPricePerUnit(stock.getAveragePurchasePrice());
    transaction.setCurrency(stock.getCurrency());
    transaction.setFxRateToGbp(fxRate);
    transaction.setOccurredAt(occurredAt);

    assetTransactionRepository.save(transaction);

    // 7 Update snapshot quantity
    BigDecimal newQty = currentQty.subtract(quantity);
    stock.setQuantity(newQty);
    stock.setLastTransactionAt(occurredAt);

    // 8 Delete if fully sold
    if (newQty.compareTo(BigDecimal.ZERO) == 0) {
      stock.setQuantity(BigDecimal.ZERO);
      stock.setIsDeleted(true);
      stock.setLastTransactionAt(occurredAt);

      UserStock saved = userStockRepository.save(stock);
      return UserStockMapper.mapToUserStockDto(saved);
    }

    // 9 Persist snapshot
    UserStock saved = userStockRepository.save(stock);
    return UserStockMapper.mapToUserStockDto(saved);
  }

}
