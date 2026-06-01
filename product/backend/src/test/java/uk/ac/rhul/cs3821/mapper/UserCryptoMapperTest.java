package uk.ac.rhul.cs3821.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.dto.UserCryptoDto;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserCrypto;

/**
 * Unit tests for the UserCryptoMapper class.
 */
public class UserCryptoMapperTest {

  private UserCrypto userCrypto;
  private UserDto userDto;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    now = LocalDateTime.now();

    // Create a User entity
    User user = new User();
    user.setId(1L);
    user.setFirebaseUid("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1");
    user.setFirstName("Alice");
    user.setLastName("Johnson");
    user.setUsername("alicej");
    user.setEmail("alice.johnson@example.com");
    user.setActive(true);
    user.setRole("standard");

    // Create a UserCrypto entity
    userCrypto = new UserCrypto();
    userCrypto.setHoldingId(10L);
    userCrypto.setUser(user);
    userCrypto.setSymbol("BTC");
    userCrypto.setName("Bitcoin");
    userCrypto.setQuantity(BigDecimal.valueOf(2.5));
    userCrypto.setCurrency("USD");
    userCrypto.setAveragePurchasePrice(BigDecimal.valueOf(45000.00));
    userCrypto.setLastTransactionAt(now.minusDays(1));
    userCrypto.setLastUpdatedPriceAt(now);
    userCrypto.setIsDeleted(false);
    userCrypto.setNotes("Long-term investment.");
    userCrypto.setCreatedAt(now.minusDays(5));
    userCrypto.setUpdatedAt(now);

    // Create a corresponding UserDto
    userDto = new UserDto();
    userDto.setId(1L);
    userDto.setFirebaseUid("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1");
    userDto.setFirstName("Alice");
    userDto.setLastName("Johnson");
    userDto.setUsername("alicej");
    userDto.setEmail("alice.johnson@example.com");
    userDto.setActive(true);
    userDto.setRole("standard");
  }

  @Test
  void testConstructorCoverage() {
    // Ensures default constructor is invoked for coverage
    new UserCryptoMapper();
  }

  @Test
  void testMapToUserCryptoDto() {
    UserCryptoDto dto = UserCryptoMapper.mapToUserCryptoDto(userCrypto);

    assertNotNull(dto);
    assertEquals(10L, dto.getHoldingId());
    assertEquals("BTC", dto.getSymbol());
    assertEquals("Bitcoin", dto.getName());
    assertEquals(BigDecimal.valueOf(2.5), dto.getQuantity());
    assertEquals("USD", dto.getCurrency());
    assertEquals(BigDecimal.valueOf(45000.00), dto.getAveragePurchasePrice());
    assertEquals(false, dto.getIsDeleted());
    assertEquals("Long-term investment.", dto.getNotes());
    assertEquals(1L, dto.getUser().getId());
    assertEquals("Alice", dto.getUser().getFirstName());
  }

  @Test
  void testMapToUserCrypto() {
    UserCryptoDto dto = new UserCryptoDto(
            20L,
            userDto,
            "ETH",
            "Ethereum",
            BigDecimal.valueOf(3.0),
            "USD",
            BigDecimal.valueOf(2500.00),
            now.minusDays(2),
            now,
            false,
            "Ethereum staking position",
            now.minusDays(5),
            now
    );

    UserCrypto entity = UserCryptoMapper.mapToUserCrypto(dto);

    assertNotNull(entity);
    assertEquals(20L, entity.getHoldingId());
    assertEquals("ETH", entity.getSymbol());
    assertEquals("Ethereum", entity.getName());
    assertEquals(BigDecimal.valueOf(3.0), entity.getQuantity());
    assertEquals("USD", entity.getCurrency());
    assertEquals(BigDecimal.valueOf(2500.00), entity.getAveragePurchasePrice());
    assertEquals(false, entity.getIsDeleted());
    assertEquals("Ethereum staking position", entity.getNotes());
    assertNotNull(entity.getUser());
    assertEquals(1L, entity.getUser().getId());
    assertEquals("alicej", entity.getUser().getUsername());
  }

  @Test
  void testMapNullInputs() {
    assertNull(UserCryptoMapper.mapToUserCrypto(null));
    assertNull(UserCryptoMapper.mapToUserCryptoDto(null));
  }
}
