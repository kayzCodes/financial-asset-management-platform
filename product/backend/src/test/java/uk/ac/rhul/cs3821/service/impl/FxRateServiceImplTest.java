package uk.ac.rhul.cs3821.service.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class FxRateServiceImplTest {

  private FxRateServiceImpl service;

  @BeforeEach
  void setUp() throws Exception {

    service = new FxRateServiceImpl(WebClient.builder());

    Map<LocalDate, Map<String, BigDecimal>> cache = new HashMap<>();

    Map<String, BigDecimal> dayRates = new HashMap<>();
    dayRates.put("GBP", new BigDecimal("0.85"));
    dayRates.put("USD", new BigDecimal("1.10"));

    cache.put(LocalDate.of(2024, 1, 1), dayRates);

    Field cacheField =
            FxRateServiceImpl.class.getDeclaredField("ratesCache");

    cacheField.setAccessible(true);
    cacheField.set(service, cache);
  }

  @Test
  void getFxToGbp_returnsCorrectConversion() {

    BigDecimal result = service.getFxToGbp(
            "USD",
            LocalDateTime.of(2024, 1, 1, 0, 0)
    );

    BigDecimal expected =
            new BigDecimal("0.85")
                    .divide(new BigDecimal("1.10"), 12,
                            java.math.RoundingMode.HALF_UP);

    assertEquals(0, expected.compareTo(result));
  }

  @Test
  void getFxToGbp_returnsOneForGbp() {

    BigDecimal result =
            service.getFxToGbp("GBP", LocalDateTime.now());

    assertEquals(BigDecimal.ONE, result);
  }

  @Test
  void getFxToGbp_throwsWhenCurrencyMissing() {

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> service.getFxToGbp(null, LocalDateTime.now())
    );

    assertEquals("Currency required", ex.getMessage());
  }

  @Test
  void getFxToGbp_throwsWhenRateMissing() {

    RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> service.getFxToGbp(
                    "JPY",
                    LocalDateTime.of(2024, 1, 1, 0, 0)
            )
    );

    assertTrue(ex.getMessage().contains("FX rate unavailable"));
  }

  @Test
  void getFxToGbp_findsClosestPreviousDate() {

    BigDecimal result = service.getFxToGbp(
            "USD",
            LocalDateTime.of(2024, 1, 3, 0, 0)
    );

    assertNotNull(result);
  }
}