package uk.ac.rhul.cs3821.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.enums.AssetType;
import uk.ac.rhul.cs3821.enums.TransactionType;

class AssetTransactionTest {

  @Test
  void testEntityCreation() {

    AssetTransaction transaction = new AssetTransaction();

    User user = new User();
    UserStock stock = new UserStock();

    LocalDateTime now = LocalDateTime.now();

    transaction.setUser(user);
    transaction.setStockHolding(stock);
    transaction.setAssetType(AssetType.STOCK);
    transaction.setTransactionType(TransactionType.BUY);
    transaction.setQuantity(BigDecimal.TEN);
    transaction.setPricePerUnit(new BigDecimal("150"));
    transaction.setCurrency("USD");
    transaction.setFxRateToGbp(new BigDecimal("0.80"));
    transaction.setOccurredAt(now);

    assertEquals(user, transaction.getUser());
    assertEquals(stock, transaction.getStockHolding());
    assertEquals(AssetType.STOCK, transaction.getAssetType());
    assertEquals(TransactionType.BUY, transaction.getTransactionType());
    assertEquals(BigDecimal.TEN, transaction.getQuantity());
    assertEquals("USD", transaction.getCurrency());
  }
}
