package uk.ac.rhul.cs3821.mapper;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.dto.StockFundamentalsDto;
import uk.ac.rhul.cs3821.entity.StockFundamentals;

class StockFundamentalsMapperTest {

  @Test
  void mapToStockFundamentalsDto_mapsAllExpectedFields() {
    // Arrange
    StockFundamentals entity = new StockFundamentals();
    entity.setId(1L);
    entity.setSymbol("AAPL");
    entity.setMarketCap("2500000000000");
    entity.setPeRatio("28.5");
    entity.setEps("6.13");
    entity.setSector("Technology");
    entity.setIndustry("Consumer Electronics");
    entity.setDescription("Apple Inc description");
    entity.setLastUpdatedAt(LocalDateTime.now());

    // Act
    StockFundamentalsDto dto =
            StockFundamentalsMapper.mapToStockFundamentalsDto(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(entity.getId(), dto.getId());
    assertEquals(entity.getSymbol(), dto.getSymbol());
    assertEquals(entity.getMarketCap(), dto.getMarketCap());
    assertEquals(entity.getPeRatio(), dto.getPeRatio());
    assertEquals(entity.getEps(), dto.getEps());
    assertEquals(entity.getSector(), dto.getSector());
    assertEquals(entity.getIndustry(), dto.getIndustry());
    assertEquals(entity.getDescription(), dto.getDescription());
    assertEquals(entity.getLastUpdatedAt(), dto.getLastUpdatedAt());
  }

  @Test
  void mapToStockFundamentalsDto_returnsNullWhenEntityIsNull() {
    assertNull(StockFundamentalsMapper.mapToStockFundamentalsDto(null));
  }

  @Test
  void mapToStockFundamentals_mapsAllExpectedFields() {
    // Arrange
    LocalDateTime now = LocalDateTime.now();

    StockFundamentalsDto dto = new StockFundamentalsDto(
            2L,
            "MSFT",
            "2300000000000",
            "32.1",
            "9.21",
            "Technology",
            "Software",
            "Microsoft description",
            now
    );

    // Act
    StockFundamentals entity =
            StockFundamentalsMapper.mapToStockFundamentals(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(dto.getId(), entity.getId());
    assertEquals(dto.getSymbol(), entity.getSymbol());
    assertEquals(dto.getMarketCap(), entity.getMarketCap());
    assertEquals(dto.getPeRatio(), entity.getPeRatio());
    assertEquals(dto.getEps(), entity.getEps());
    assertEquals(dto.getSector(), entity.getSector());
    assertEquals(dto.getIndustry(), entity.getIndustry());
    assertEquals(dto.getDescription(), entity.getDescription());
    assertEquals(dto.getLastUpdatedAt(), entity.getLastUpdatedAt());
  }

  @Test
  void mapToStockFundamentals_returnsNullWhenDtoIsNull() {
    assertNull(StockFundamentalsMapper.mapToStockFundamentals(null));
  }
}
