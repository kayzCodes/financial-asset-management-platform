package uk.ac.rhul.cs3821.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.dto.CryptoFundamentalsDto;
import uk.ac.rhul.cs3821.entity.CryptoFundamentals;

class CryptoFundamentalsMapperTest {

  @Test
  void mapToCryptoFundamentalsDto_mapsAllExpectedFields() {
    // Arrange
    CryptoFundamentals entity = new CryptoFundamentals();
    entity.setId(1L);
    entity.setSymbol("BTC");
    entity.setName("Bitcoin");
    entity.setMarketCap(BigDecimal.valueOf(1_000_000_000));
    entity.setDescription("Crypto description");
    entity.setLastUpdatedAt(LocalDateTime.now());

    // Act
    CryptoFundamentalsDto dto =
            CryptoFundamentalsMapper.mapToCryptoFundamentalsDto(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(entity.getId(), dto.getId());
    assertEquals(entity.getSymbol(), dto.getSymbol());
    assertEquals(entity.getName(), dto.getName());
    assertEquals(entity.getMarketCap(), dto.getMarketCap());
    assertEquals(entity.getDescription(), dto.getDescription());
    assertEquals(entity.getLastUpdatedAt(), dto.getLastUpdatedAt());
  }

  @Test
  void mapToCryptoFundamentalsDto_returnsNullWhenEntityIsNull() {
    assertNull(CryptoFundamentalsMapper.mapToCryptoFundamentalsDto(null));
  }

  @Test
  void mapToCryptoFundamentals_mapsAllExpectedFields() {
    // Arrange
    CryptoFundamentalsDto dto = new CryptoFundamentalsDto(
            2L,
            "ETH",
            "Ethereum",
            BigDecimal.valueOf(500_000_000),
            "Ethereum description",
            LocalDateTime.now()
    );

    // Act
    CryptoFundamentals entity =
            CryptoFundamentalsMapper.mapToCryptoFundamentals(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(dto.getId(), entity.getId());
    assertEquals(dto.getSymbol(), entity.getSymbol());
    assertEquals(dto.getName(), entity.getName());
    assertEquals(dto.getMarketCap(), entity.getMarketCap());
    assertEquals(dto.getDescription(), entity.getDescription());

    // IMPORTANT: timestamps must NOT be set by mapper
    assertNull(entity.getCreatedAt());
    assertNull(entity.getLastUpdatedAt());
  }

  @Test
  void mapToCryptoFundamentals_returnsNullWhenDtoIsNull() {
    assertNull(CryptoFundamentalsMapper.mapToCryptoFundamentals(null));
  }
}
