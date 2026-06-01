package uk.ac.rhul.cs3821.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the UserStock entity class.
 */
public class UserStockTest {

  private UserStock userStock;
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
    user.setCurrency("USD");
    user.setRole("standard");
    user.setPreferredLanguage("en");
    user.setTimezone("Europe/London");
    user.setTheme("system");
    user.setCreatedAt(LocalDateTime.now().minusDays(10));
    user.setUpdatedAt(LocalDateTime.now().minusDays(1));

    userStock = new UserStock();
  }

  @Test
  void testNoArgsConstructorAndSetters() {
    LocalDateTime now = LocalDateTime.now();

    userStock.setHoldingId(100L);
    userStock.setUser(user);
    userStock.setTickerSymbol("AAPL");
    userStock.setCompanyName("Apple Inc.");
    userStock.setQuantity(BigDecimal.valueOf(50));
    userStock.setCurrency("USD");
    userStock.setAveragePurchasePrice(BigDecimal.valueOf(150.25));
    userStock.setLastTransactionAt(now.minusDays(1));
    userStock.setLastUpdatedPriceAt(now);
    userStock.setIsDeleted(false);
    userStock.setNotes("Long-term investment in Apple.");
    userStock.setCreatedAt(now.minusDays(3));
    userStock.setUpdatedAt(now);

    assertEquals(100L, userStock.getHoldingId());
    assertEquals(user, userStock.getUser());
    assertEquals("AAPL", userStock.getTickerSymbol());
    assertEquals("Apple Inc.", userStock.getCompanyName());
    assertEquals(BigDecimal.valueOf(50), userStock.getQuantity());
    assertEquals("USD", userStock.getCurrency());
    assertEquals(BigDecimal.valueOf(150.25), userStock.getAveragePurchasePrice());
    assertEquals(now.minusDays(1), userStock.getLastTransactionAt());
    assertEquals(now, userStock.getLastUpdatedPriceAt());
    assertEquals(false, userStock.getIsDeleted());
    assertEquals("Long-term investment in Apple.", userStock.getNotes());
    assertEquals(now.minusDays(3), userStock.getCreatedAt());
    assertEquals(now, userStock.getUpdatedAt());
  }

  @Test
  void testAllArgsConstructorAndGetters() {
    LocalDateTime now = LocalDateTime.now();

    UserStock constructed = new UserStock(
            200L,
            user,
            "TSLA",
            "Tesla Inc.",
            BigDecimal.valueOf(25),
            "USD",
            BigDecimal.valueOf(700.00),
            now.minusDays(2),
            now,
            false,
            "Holding for EV market growth.",
            now.minusDays(5),
            now
    );

    assertEquals(200L, constructed.getHoldingId());
    assertEquals(user, constructed.getUser());
    assertEquals("TSLA", constructed.getTickerSymbol());
    assertEquals("Tesla Inc.", constructed.getCompanyName());
    assertEquals(BigDecimal.valueOf(25), constructed.getQuantity());
    assertEquals("USD", constructed.getCurrency());
    assertEquals(BigDecimal.valueOf(700.00), constructed.getAveragePurchasePrice());
    assertEquals(now.minusDays(2), constructed.getLastTransactionAt());
    assertEquals(now, constructed.getLastUpdatedPriceAt());
    assertEquals(false, constructed.getIsDeleted());
    assertEquals("Holding for EV market growth.", constructed.getNotes());
    assertEquals(now.minusDays(5), constructed.getCreatedAt());
    assertEquals(now, constructed.getUpdatedAt());
  }
}
