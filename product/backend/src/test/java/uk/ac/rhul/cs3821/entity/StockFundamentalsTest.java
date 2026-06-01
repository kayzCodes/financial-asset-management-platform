package uk.ac.rhul.cs3821.entity;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class StockFundamentalsTest {

  @Test
  void gettersAndSetters_workCorrectly() {
    StockFundamentals fundamentals = new StockFundamentals();

    LocalDateTime now = LocalDateTime.now();

    fundamentals.setId(1L);
    fundamentals.setSymbol("AAPL");
    fundamentals.setMarketCap("2500000000000");
    fundamentals.setPeRatio("28.5");
    fundamentals.setEps("6.13");
    fundamentals.setSector("Technology");
    fundamentals.setIndustry("Consumer Electronics");
    fundamentals.setDescription("Apple Inc designs consumer electronics.");
    fundamentals.setLastUpdatedAt(now);

    assertEquals(1L, fundamentals.getId());
    assertEquals("AAPL", fundamentals.getSymbol());
    assertEquals("2500000000000", fundamentals.getMarketCap());
    assertEquals("28.5", fundamentals.getPeRatio());
    assertEquals("6.13", fundamentals.getEps());
    assertEquals("Technology", fundamentals.getSector());
    assertEquals("Consumer Electronics", fundamentals.getIndustry());
    assertEquals("Apple Inc designs consumer electronics.", fundamentals.getDescription());
    assertEquals(now, fundamentals.getLastUpdatedAt());
  }

  @Test
  void onUpdate_overwritesExistingTimestamp() {
    StockFundamentals fundamentals = new StockFundamentals();

    LocalDateTime before = LocalDateTime.now().minusDays(1);
    fundamentals.setLastUpdatedAt(before);

    fundamentals.onUpdate();

    assertTrue(fundamentals.getLastUpdatedAt().isAfter(before));
  }

}
