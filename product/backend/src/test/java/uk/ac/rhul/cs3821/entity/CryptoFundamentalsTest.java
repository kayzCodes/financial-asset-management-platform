package uk.ac.rhul.cs3821.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CryptoFundamentalsTest {

  @Test
  void prePersist_setsCreatedAndUpdatedTimestamps() {
    CryptoFundamentals fundamentals = new CryptoFundamentals();
    fundamentals.setSymbol("BTC");
    fundamentals.setName("Bitcoin");
    fundamentals.setMarketCap(BigDecimal.valueOf(1_000_000_000));
    fundamentals.setDescription("Test description");

    // Act
    fundamentals.onCreate();

    // Assert
    assertNotNull(fundamentals.getCreatedAt());
    assertNotNull(fundamentals.getLastUpdatedAt());
    assertEquals(
            fundamentals.getCreatedAt(),
            fundamentals.getLastUpdatedAt(),
            "createdAt and lastUpdatedAt should be equal on creation"
    );
  }

  @Test
  void preUpdate_updatesLastUpdatedTimestampOnly() throws InterruptedException {
    CryptoFundamentals fundamentals = new CryptoFundamentals();
    fundamentals.setSymbol("ETH");
    fundamentals.setName("Ethereum");

    // Initial persist
    fundamentals.onCreate();
    LocalDateTime createdAt = fundamentals.getCreatedAt();
    LocalDateTime initialUpdatedAt = fundamentals.getLastUpdatedAt();

    // Ensure time difference
    Thread.sleep(5);

    // Act
    fundamentals.onUpdate();

    // Assert
    assertEquals(createdAt, fundamentals.getCreatedAt(), "createdAt must not change");
    assertTrue(
            fundamentals.getLastUpdatedAt().isAfter(initialUpdatedAt),
            "lastUpdatedAt must be updated on update"
    );
  }

  @Test
  void gettersAndSetters_workCorrectly() {
    CryptoFundamentals fundamentals = new CryptoFundamentals();

    fundamentals.setId(1L);
    fundamentals.setSymbol("BTC");
    fundamentals.setName("Bitcoin");
    fundamentals.setMarketCap(BigDecimal.valueOf(500_000_000));
    fundamentals.setDescription("Crypto description");

    assertEquals(1L, fundamentals.getId());
    assertEquals("BTC", fundamentals.getSymbol());
    assertEquals("Bitcoin", fundamentals.getName());
    assertEquals(BigDecimal.valueOf(500_000_000), fundamentals.getMarketCap());
    assertEquals("Crypto description", fundamentals.getDescription());
  }
}
