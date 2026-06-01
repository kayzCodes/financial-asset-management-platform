package uk.ac.rhul.cs3821.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the UserCrypto entity class.
 */
public class UserCryptoTest {

  private UserCrypto userCrypto;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setFirstName("Alice");
    user.setLastName("Johnson");
    user.setUsername("alicej");
    user.setEmail("alice.johnson@example.com");
    user.setActive(true);
    user.setRole("standard");
    user.setCurrency("GBP");
    user.setPreferredLanguage("en");
    user.setTimezone("Europe/London");
    user.setTheme("system");
    user.setCreatedAt(LocalDateTime.now().minusDays(10));
    user.setUpdatedAt(LocalDateTime.now().minusDays(1));

    userCrypto = new UserCrypto();
  }

  @Test
  void testNoArgsConstructorAndSetters() {
    LocalDateTime now = LocalDateTime.now();

    userCrypto.setHoldingId(10L);
    userCrypto.setUser(user);
    userCrypto.setSymbol("BTC");
    userCrypto.setName("Bitcoin");
    userCrypto.setQuantity(BigDecimal.valueOf(1.5));
    userCrypto.setCurrency("USD");
    userCrypto.setAveragePurchasePrice(BigDecimal.valueOf(45000.50));
    userCrypto.setLastTransactionAt(now.minusDays(1));
    userCrypto.setLastUpdatedPriceAt(now);
    userCrypto.setIsDeleted(false);
    userCrypto.setNotes("Long-term investment.");
    userCrypto.setCreatedAt(now.minusDays(2));
    userCrypto.setUpdatedAt(now);

    assertEquals(10L, userCrypto.getHoldingId());
    assertEquals(user, userCrypto.getUser());
    assertEquals("BTC", userCrypto.getSymbol());
    assertEquals("Bitcoin", userCrypto.getName());
    assertEquals(BigDecimal.valueOf(1.5), userCrypto.getQuantity());
    assertEquals("USD", userCrypto.getCurrency());
    assertEquals(BigDecimal.valueOf(45000.50), userCrypto.getAveragePurchasePrice());
    assertEquals(now.minusDays(1), userCrypto.getLastTransactionAt());
    assertEquals(now, userCrypto.getLastUpdatedPriceAt());
    assertEquals(false, userCrypto.getIsDeleted());
    assertEquals("Long-term investment.", userCrypto.getNotes());
    assertEquals(now.minusDays(2), userCrypto.getCreatedAt());
    assertEquals(now, userCrypto.getUpdatedAt());
  }

  @Test
  void testAllArgsConstructorAndGetters() {
    LocalDateTime now = LocalDateTime.now();

    UserCrypto constructed = new UserCrypto(
            20L,
            user,
            "ETH",
            "Ethereum",
            BigDecimal.valueOf(3.0),
            "USD",
            BigDecimal.valueOf(2500.00),
            now.minusDays(1),
            now,
            false,
            "Ethereum staking position",
            now.minusDays(2),
            now
    );

    assertEquals(20L, constructed.getHoldingId());
    assertEquals(user, constructed.getUser());
    assertEquals("ETH", constructed.getSymbol());
    assertEquals("Ethereum", constructed.getName());
    assertEquals(BigDecimal.valueOf(3.0), constructed.getQuantity());
    assertEquals("USD", constructed.getCurrency());
    assertEquals(BigDecimal.valueOf(2500.00), constructed.getAveragePurchasePrice());
    assertEquals(now.minusDays(1), constructed.getLastTransactionAt());
    assertEquals(now, constructed.getLastUpdatedPriceAt());
    assertEquals(false, constructed.getIsDeleted());
    assertEquals("Ethereum staking position", constructed.getNotes());
    assertEquals(now.minusDays(2), constructed.getCreatedAt());
    assertEquals(now, constructed.getUpdatedAt());
  }
}
