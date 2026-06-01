package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.dto.ChartPointDto;
import uk.ac.rhul.cs3821.dto.CryptoDetailsDto;
import uk.ac.rhul.cs3821.dto.CryptoFundamentalsDto;
import uk.ac.rhul.cs3821.dto.CryptoTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserCryptoDto;
import uk.ac.rhul.cs3821.entity.AssetTransaction;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.enums.AssetType;
import uk.ac.rhul.cs3821.enums.TransactionType;
import uk.ac.rhul.cs3821.mapper.UserCryptoMapper;
import uk.ac.rhul.cs3821.repository.AssetTransactionRepository;
import uk.ac.rhul.cs3821.repository.UserCryptoRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.service.FxRateService;
import uk.ac.rhul.cs3821.service.UserCryptoService;

/**
 * Service implementation for managing user cryptocurrency operations.
 */
@Service
@AllArgsConstructor
public class UserCryptoServiceImpl implements UserCryptoService {

  private final UserRepository userRepository;
  private final UserCryptoRepository userCryptoRepository;
  private final CryptoFundamentalsServiceImpl cryptoFundamentalsServiceImpl;
  private final FxRateService fxRateService;
  private final AssetTransactionRepository assetTransactionRepository;
  private final AlphaVantageDailySeriesProvider alphaVantageDailySeriesProvider;
  private final LivePriceServiceImpl livePriceServiceImpl;

  /**
   * Creates a new crypto holding with an initial buy transaction.
   *
   * @param firebaseUid   the authenticated user identifier
   * @param userCryptoDto crypto holding details
   * @param initialBuy    initial buy transaction request
   * @return created {@link UserCryptoDto}
   */
  @Override
  public UserCryptoDto addCrypto(
          String firebaseUid,
          UserCryptoDto userCryptoDto,
          CryptoTransactionRequestDto initialBuy
  ) {

    // 1 xValidate user
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

    // 2 Map DTO → entity
    UserCrypto userCrypto = UserCryptoMapper.mapToUserCrypto(userCryptoDto);
    userCrypto.setUser(user);

    // All crypto stored in USD in your system
    userCrypto.setCurrency("USD");

    // Snapshot values must reflect the transaction
    userCrypto.setQuantity(quantity);
    userCrypto.setAveragePurchasePrice(pricePerUnit);
    userCrypto.setLastTransactionAt(occurredAt);

    UserCrypto savedCrypto = userCryptoRepository.save(userCrypto);

    // 3 Fetch FX (USD → GBP)
    BigDecimal fxRate =
            fxRateService.getFxToGbp(savedCrypto.getCurrency(), occurredAt);

    // 4 Create BUY transaction
    AssetTransaction transaction = new AssetTransaction();
    transaction.setUser(user);
    transaction.setCryptoHolding(savedCrypto);
    transaction.setAssetType(AssetType.CRYPTO);
    transaction.setTransactionType(TransactionType.BUY);
    transaction.setQuantity(quantity);
    transaction.setPricePerUnit(pricePerUnit);
    transaction.setCurrency(savedCrypto.getCurrency());
    transaction.setFxRateToGbp(fxRate);
    transaction.setOccurredAt(occurredAt);

    assetTransactionRepository.save(transaction);

    return UserCryptoMapper.mapToUserCryptoDto(savedCrypto);
  }

  /**
   * Retrieves all crypto holdings belonging to the user.
   *
   * @param firebaseUid the Firebase UID of the user
   * @return list of crypto holdings as DTOs
   */
  @Override
  public List<UserCryptoDto> getAllUserCryptoByFirebaseUid(String firebaseUid) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException(
                    "User not found for Firebase UID: " + firebaseUid));

    Long userId = user.getId();

    List<UserCrypto> cryptos = userCryptoRepository.findByUserIdAndIsDeletedFalse(userId);

    return cryptos.stream()
            .map(UserCryptoMapper::mapToUserCryptoDto)
            .collect(Collectors.toList());
  }

  /**
   * Deletes a crypto holding after validating the authenticated user and ownership.
   *
   * @param firebaseUid the authenticated user identifier
   * @param holdingId   the crypto holding identifier
   * @return the deleted holding as {@link UserCryptoDto}
   * @throws RuntimeException if the user, holding, or ownership validation fails
   */
  @Override
  public UserCryptoDto deleteCrypto(String firebaseUid, Long holdingId) {

    // 1. Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException(
                    "User not found for Firebase UID: " + firebaseUid));

    // 2. Fetch crypto
    UserCrypto crypto = userCryptoRepository.findById(holdingId)
            .orElseThrow(() -> new RuntimeException(
                    "Crypto not found for ID: " + holdingId));

    // 3. Ownership check
    if (!crypto.getUser().getId().equals(user.getId())) {
      throw new RuntimeException(
              "Crypto does not belong to the authenticated user");
    }

    BigDecimal quantity = crypto.getQuantity();

    // 4. If position still exists → auto close
    if (quantity.compareTo(BigDecimal.ZERO) > 0) {

      BigDecimal marketPrice =
              livePriceServiceImpl.getCurrentCryptoPrice(crypto.getSymbol());

      if (marketPrice == null) {
        throw new RuntimeException("Unable to fetch current crypto price");
      }

      LocalDateTime occurredAt = LocalDateTime.now();

      BigDecimal fxRate =
              fxRateService.getFxToGbp(crypto.getCurrency(), occurredAt);

      AssetTransaction transaction = new AssetTransaction();
      transaction.setUser(user);
      transaction.setCryptoHolding(crypto);
      transaction.setAssetType(AssetType.CRYPTO);
      transaction.setTransactionType(TransactionType.SELL);
      transaction.setQuantity(quantity);
      transaction.setPricePerUnit(marketPrice);
      transaction.setCurrency(crypto.getCurrency());
      transaction.setFxRateToGbp(fxRate);
      transaction.setOccurredAt(occurredAt);

      assetTransactionRepository.save(transaction);

      crypto.setQuantity(BigDecimal.ZERO);
      crypto.setIsDeleted(true);
      crypto.setLastTransactionAt(occurredAt);

      UserCrypto saved = userCryptoRepository.save(crypto);

      return UserCryptoMapper.mapToUserCryptoDto(saved);
    }

    // 5. If already empty → just hide
    crypto.setIsDeleted(true);

    UserCrypto saved = userCryptoRepository.save(crypto);

    return UserCryptoMapper.mapToUserCryptoDto(saved);
  }

  /**
   * Generates a comprehensive detail view for a specific cryptocurrency holding.
   * * Validates user ownership, then aggregates fundamental data from the database
   * with real-time market data from Alpha Vantage. Performs portfolio calculations
   * including current valuation and profit/loss metrics, and processes historical
   * data into a sanitized time-series chart.
   *
   * @param firebaseUid the unique identifier of the authenticated user.
   * @param holdingId   the primary key of the crypto holding.
   * @return {@link CryptoDetailsDto} with metadata, metrics, and chart data
   * @throws RuntimeException if the user or holding is not found, or if ownership validation fails.
   */
  @Override
  public CryptoDetailsDto getCryptoDetail(
          String firebaseUid,
          Long holdingId) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() ->
                    new RuntimeException(
                            "User not found for Firebase UID: "
                                    + firebaseUid));

    UserCrypto holding = userCryptoRepository.findById(holdingId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Crypto holding not found for ID: "
                                    + holdingId));

    if (!holding.getUser().getId().equals(user.getId())) {
      throw new RuntimeException(
              "Crypto holding does not belong to the authenticated user");
    }

    String symbol = holding.getSymbol();

    CryptoFundamentalsDto fundamentals =
            cryptoFundamentalsServiceImpl.getCryptoFundamentals(symbol);

    Map<LocalDate, BigDecimal> series =
            alphaVantageDailySeriesProvider.getCryptoDailyCloseSeries(symbol);

    BigDecimal latestClose = null;
    BigDecimal currentValue = null;
    BigDecimal priceDifference = null;
    BigDecimal percentageChangeFromAveragePrice = null;

    BigDecimal percentageChange = null;

    List<ChartPointDto> chartData = List.of();

    if (series != null && !series.isEmpty()) {

      List<Map.Entry<LocalDate, BigDecimal>> entries =
              new ArrayList<>(series.entrySet());

      entries.sort(Map.Entry.comparingByKey());

      latestClose = entries.get(entries.size() - 1).getValue();

      if (latestClose != null) {

        currentValue =
                latestClose.multiply(holding.getQuantity());

        BigDecimal totalCost =
                holding.getAveragePurchasePrice()
                        .multiply(holding.getQuantity());

        priceDifference = currentValue.subtract(totalCost);

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

    return new CryptoDetailsDto(
            holding.getSymbol(),
            holding.getName(),
            holding.getQuantity(),
            holding.getAveragePurchasePrice(),
            latestClose,
            percentageChange,
            currentValue,
            priceDifference,
            percentageChangeFromAveragePrice,
            chartData,
            fundamentals
    );
  }

  /**
   * Records a sell transaction for a crypto holding and updates its snapshot state.
   *
   * @param firebaseUid the authenticated user identifier
   * @param holdingId   crypto holding identifier
   * @param request     sell transaction details
   * @return updated {@link UserCryptoDto}
   */
  @Override
  public UserCryptoDto updateCryptoSell(
          String firebaseUid,
          Long holdingId,
          CryptoTransactionRequestDto request
  ) {

    // 1 Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2 Fetch crypto holding
    UserCrypto crypto = userCryptoRepository.findById(holdingId)
            .orElseThrow(() -> new RuntimeException("Crypto holding not found"));

    // 3 Ownership check
    if (!crypto.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Crypto holding does not belong to user");
    }

    BigDecimal quantity = request.getQuantity();

    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("Sell quantity must be positive");
    }

    BigDecimal currentQty = crypto.getQuantity();

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

    // 5 Fetch FX (USD → GBP typically)
    BigDecimal fxRate =
            fxRateService.getFxToGbp(crypto.getCurrency(), occurredAt);

    // 6 Persist SELL transaction
    AssetTransaction transaction = new AssetTransaction();
    transaction.setUser(user);
    transaction.setCryptoHolding(crypto);
    transaction.setAssetType(AssetType.CRYPTO);
    transaction.setTransactionType(TransactionType.SELL);
    transaction.setQuantity(quantity);
    transaction.setPricePerUnit(crypto.getAveragePurchasePrice());
    transaction.setCurrency(crypto.getCurrency());
    transaction.setFxRateToGbp(fxRate);
    transaction.setOccurredAt(occurredAt);

    assetTransactionRepository.save(transaction);

    // 7 Update snapshot
    BigDecimal newQty = currentQty.subtract(quantity);
    crypto.setQuantity(newQty);
    crypto.setLastTransactionAt(occurredAt);

    // 8 Delete if fully sold
    if (newQty.compareTo(BigDecimal.ZERO) == 0) {

      crypto.setQuantity(BigDecimal.ZERO);
      crypto.setIsDeleted(true);
      crypto.setLastTransactionAt(occurredAt);

      UserCrypto saved = userCryptoRepository.save(crypto);

      return UserCryptoMapper.mapToUserCryptoDto(saved);
    }

    // 9 Persist snapshot
    UserCrypto saved = userCryptoRepository.save(crypto);
    return UserCryptoMapper.mapToUserCryptoDto(saved);
  }

  /**
   * Records a buy transaction for a crypto holding and updates its snapshot state.
   *
   * @param firebaseUid the authenticated user identifier
   * @param holdingId   crypto holding identifier
   * @param request     buy transaction details
   * @return updated {@link UserCryptoDto}
   */
  @Override
  public UserCryptoDto updateCryptoBuy(
          String firebaseUid,
          Long holdingId,
          CryptoTransactionRequestDto request
  ) {

    // 1 Validate user
    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2 Fetch crypto holding
    UserCrypto crypto = userCryptoRepository.findById(holdingId)
            .orElseThrow(() -> new RuntimeException("Crypto holding not found"));

    // 3 Ownership check
    if (!crypto.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("Crypto holding does not belong to user");
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

    // 5 Fetch FX (USD → GBP typically)
    BigDecimal fxRate =
            fxRateService.getFxToGbp(crypto.getCurrency(), occurredAt);

    // Persist BUY transaction
    AssetTransaction transaction = new AssetTransaction();
    transaction.setUser(user);
    transaction.setCryptoHolding(crypto);
    transaction.setAssetType(AssetType.CRYPTO);
    transaction.setTransactionType(TransactionType.BUY);
    transaction.setQuantity(quantity);
    transaction.setPricePerUnit(pricePerUnit);
    transaction.setCurrency(crypto.getCurrency());
    transaction.setFxRateToGbp(fxRate);
    transaction.setOccurredAt(occurredAt);

    assetTransactionRepository.save(transaction);

    // 7 Existing snapshot average logic (unchanged)
    BigDecimal oldQty = crypto.getQuantity();
    BigDecimal oldAvg = crypto.getAveragePurchasePrice();

    BigDecimal oldTotalCost = oldAvg.multiply(oldQty);
    BigDecimal newBuyCost = pricePerUnit.multiply(quantity);

    BigDecimal newTotalQty = oldQty.add(quantity);

    BigDecimal newAvgPrice = oldTotalCost.add(newBuyCost)
            .divide(newTotalQty, 6, RoundingMode.HALF_UP);

    crypto.setQuantity(newTotalQty);
    crypto.setAveragePurchasePrice(newAvgPrice);
    crypto.setLastTransactionAt(occurredAt);

    UserCrypto saved = userCryptoRepository.save(crypto);

    return UserCryptoMapper.mapToUserCryptoDto(saved);
  }

}
