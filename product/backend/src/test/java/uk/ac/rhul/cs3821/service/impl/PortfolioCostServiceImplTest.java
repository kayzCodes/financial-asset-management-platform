package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs3821.entity.AssetTransaction;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;
import uk.ac.rhul.cs3821.enums.TransactionType;
import uk.ac.rhul.cs3821.records.HoldingCostResult;
import uk.ac.rhul.cs3821.repository.AssetTransactionRepository;

class PortfolioCostServiceImplTest {

  @Mock
  private AssetTransactionRepository assetTransactionRepository;

  @InjectMocks
  private PortfolioCostServiceImpl portfolioCostService;

  private UserStock stock;
  private UserCrypto crypto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    stock = new UserStock();
    stock.setHoldingId(10L);

    crypto = new UserCrypto();
    crypto.setHoldingId(20L);
  }

  @Test
  void calculateStockCost_returnsZeroWhenNoTransactions() {

    when(assetTransactionRepository
            .findByUser_IdAndStockHoldingOrderByOccurredAtAsc(1L, stock))
            .thenReturn(List.of());

    HoldingCostResult result =
            portfolioCostService.calculateStockCost(1L, stock);

    assertEquals(BigDecimal.ZERO, result.quantityHeld());
    assertEquals(BigDecimal.ZERO, result.costBasisGbp());
    assertEquals(BigDecimal.ZERO, result.realisedPnlGbp());
  }

  @Test
  void calculateStockCost_buyOnlyCalculatesCostBasis() {

    AssetTransaction buy = new AssetTransaction();
    buy.setTransactionType(TransactionType.BUY);
    buy.setQuantity(new BigDecimal("5"));
    buy.setPricePerUnit(new BigDecimal("100"));
    buy.setFxRateToGbp(BigDecimal.ONE);
    buy.setOccurredAt(LocalDateTime.now());

    when(assetTransactionRepository
            .findByUser_IdAndStockHoldingOrderByOccurredAtAsc(1L, stock))
            .thenReturn(List.of(buy));

    HoldingCostResult result =
            portfolioCostService.calculateStockCost(1L, stock);

    assertEquals(new BigDecimal("5"), result.quantityHeld());
    assertEquals(new BigDecimal("500"), result.costBasisGbp());
    assertEquals(BigDecimal.ZERO, result.realisedPnlGbp());
  }

  @Test
  void calculateStockCost_buyThenSellCalculatesRealisedPnL() {

    AssetTransaction buy = new AssetTransaction();
    buy.setTransactionType(TransactionType.BUY);
    buy.setQuantity(new BigDecimal("5"));
    buy.setPricePerUnit(new BigDecimal("100"));
    buy.setFxRateToGbp(BigDecimal.ONE);
    buy.setOccurredAt(LocalDateTime.now().minusDays(1));

    AssetTransaction sell = new AssetTransaction();
    sell.setTransactionType(TransactionType.SELL);
    sell.setQuantity(new BigDecimal("2"));
    sell.setPricePerUnit(new BigDecimal("150"));
    sell.setFxRateToGbp(BigDecimal.ONE);
    sell.setOccurredAt(LocalDateTime.now());

    when(assetTransactionRepository
            .findByUser_IdAndStockHoldingOrderByOccurredAtAsc(1L, stock))
            .thenReturn(List.of(buy, sell));

    HoldingCostResult result =
            portfolioCostService.calculateStockCost(1L, stock);

    assertEquals(0, result.quantityHeld().compareTo(new BigDecimal("3")));
    assertEquals(0, result.costBasisGbp().compareTo(new BigDecimal("300")));
    assertEquals(0, result.realisedPnlGbp().compareTo(new BigDecimal("100")));
  }

  @Test
  void calculateCryptoCost_buyOnlyWorksSameAsStock() {

    AssetTransaction buy = new AssetTransaction();
    buy.setTransactionType(TransactionType.BUY);
    buy.setQuantity(new BigDecimal("1"));
    buy.setPricePerUnit(new BigDecimal("20000"));
    buy.setFxRateToGbp(BigDecimal.ONE);
    buy.setOccurredAt(LocalDateTime.now());

    when(assetTransactionRepository
            .findByUser_IdAndCryptoHoldingOrderByOccurredAtAsc(1L, crypto))
            .thenReturn(List.of(buy));

    HoldingCostResult result =
            portfolioCostService.calculateCryptoCost(1L, crypto);

    assertEquals(new BigDecimal("1"), result.quantityHeld());
    assertEquals(new BigDecimal("20000"), result.costBasisGbp());
    assertEquals(BigDecimal.ZERO, result.realisedPnlGbp());
  }
}