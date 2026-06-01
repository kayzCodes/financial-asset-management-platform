package uk.ac.rhul.cs3821.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserStockDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserStock;

public class UserStockMapperTest {

  private UserDto userDto;
  private UserStock userStock;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    now = LocalDateTime.now();

    // Create User entity
    User user = new User();
    user.setId(1L);
    user.setFirebaseUid("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1");
    user.setFirstName("Alice");
    user.setLastName("Johnson");
    user.setUsername("alicej");
    user.setEmail("alice.johnson@example.com");
    user.setActive(true);
    user.setRole("standard");
    user.setPreferredLanguage("en");
    user.setTimezone("Europe/London");
    user.setCreatedAt(now.minusDays(10));
    user.setUpdatedAt(now.minusDays(2));

    // Create corresponding UserDto
    userDto = new UserDto(
            1L,
            "Hn3iuGlnuxVjcgu6Rtb7WEKSncm1",
            "Alice",
            "Johnson",
            "alicej",
            "alice.johnson@example.com",
            true,
            null,
            null,
            null,
            null,
            null,
            "USD",
            "en",
            "Europe/London",
            "standard",
            "system",
            now.minusDays(10),
            now.minusDays(2),
            null
    );

    // Create UserStock entity
    userStock = new UserStock();
    userStock.setHoldingId(100L);
    userStock.setUser(user);
    userStock.setTickerSymbol("AAPL");
    userStock.setCompanyName("Apple Inc.");
    userStock.setQuantity(BigDecimal.valueOf(50));
    userStock.setCurrency("USD");
    userStock.setAveragePurchasePrice(BigDecimal.valueOf(150.75));
    userStock.setLastTransactionAt(now.minusDays(1));
    userStock.setLastUpdatedPriceAt(now);
    userStock.setIsDeleted(false);
    userStock.setNotes("Long-term investment");
    userStock.setCreatedAt(now.minusDays(5));
    userStock.setUpdatedAt(now);
  }

  @Test
  void testConstructorCoverage() {
    new UserStockMapper();
  }

  @Test
  void testMapToUserStockDto() {
    UserStockDto dto = UserStockMapper.mapToUserStockDto(userStock);

    assertNotNull(dto);
    assertEquals(100L, dto.getHoldingId());
    assertEquals("AAPL", dto.getTickerSymbol());
    assertEquals("Apple Inc.", dto.getCompanyName());
    assertEquals(BigDecimal.valueOf(50), dto.getQuantity());
    assertEquals("USD", dto.getCurrency());
    assertEquals(BigDecimal.valueOf(150.75), dto.getAveragePurchasePrice());
    assertEquals(false, dto.getIsDeleted());
    assertEquals("Long-term investment", dto.getNotes());
    assertEquals(1L, dto.getUser().getId());
    assertEquals("alicej", dto.getUser().getUsername());
  }

  @Test
  void testMapToUserStock() {
    UserStockDto dto = new UserStockDto(
            200L,
            userDto,
            "TSLA",
            "Tesla Inc.",
            BigDecimal.valueOf(10),
            "USD",
            BigDecimal.valueOf(720.50),
            now.minusDays(3),
            now,
            false,
            "Speculative buy",
            now.minusDays(5),
            now
    );

    UserStock entity = UserStockMapper.mapToUserStock(dto);

    assertNotNull(entity);
    assertEquals(200L, entity.getHoldingId());
    assertEquals("TSLA", entity.getTickerSymbol());
    assertEquals("Tesla Inc.", entity.getCompanyName());
    assertEquals(BigDecimal.valueOf(10), entity.getQuantity());
    assertEquals("USD", entity.getCurrency());
    assertEquals(BigDecimal.valueOf(720.50), entity.getAveragePurchasePrice());
    assertEquals(false, entity.getIsDeleted());
    assertEquals("Speculative buy", entity.getNotes());
    assertNotNull(entity.getUser());
    assertEquals(1L, entity.getUser().getId());
    assertEquals("alicej", entity.getUser().getUsername());
  }

  @Test
  void testNullInputs() {
    assertNull(UserStockMapper.mapToUserStock(null));
    assertNull(UserStockMapper.mapToUserStockDto(null));
  }
}
