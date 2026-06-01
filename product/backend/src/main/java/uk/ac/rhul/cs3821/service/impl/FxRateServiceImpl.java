package uk.ac.rhul.cs3821.service.impl;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.rhul.cs3821.service.FxRateService;

/**
 * Implementation of {@link FxRateService} providing FX conversion rates to GBP.
 */
@Service
@RequiredArgsConstructor
public class FxRateServiceImpl implements FxRateService {

  private static final String ECB_URL =
          "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml";

  private static final int SCALE = 12;

  private final WebClient.Builder webClientBuilder;

  private volatile Map<LocalDate, Map<String, BigDecimal>> ratesCache;

  /**
   * Returns the FX rate converting the given currency to GBP at a specific date.
   *
   * @param currency source currency code
   * @param atDate   timestamp used to resolve the historical rate
   * @return FX rate converting the currency to GBP
   */
  @Override
  public BigDecimal getFxToGbp(String currency,
                               LocalDateTime atDate) {

    if (currency == null || currency.isBlank()) {
      throw new RuntimeException("Currency required");
    }

    if ("GBP".equalsIgnoreCase(currency)) {
      return BigDecimal.ONE;
    }

    ensureRatesLoaded();

    LocalDate date = atDate.toLocalDate();

    Map<String, BigDecimal> dailyRates =
            findClosestAvailableRates(date);

    BigDecimal eurToGbp = dailyRates.get("GBP");
    BigDecimal eurToCurrency =
            dailyRates.get(currency.toUpperCase());

    if (eurToGbp == null || eurToCurrency == null) {
      throw new RuntimeException(
              "FX rate unavailable for currency: " + currency);
    }

    return eurToGbp
            .divide(eurToCurrency, SCALE, RoundingMode.HALF_UP);
  }

  /**
   * Returns the current FX rate converting the given currency to GBP.
   *
   * @param currency source currency code
   * @return latest FX rate to GBP
   */
  @Override
  public BigDecimal getCurrentFxToGbp(String currency) {
    return getFxToGbp(currency, LocalDateTime.now());
  }

  /**
   * Ensures ECB FX rates are loaded into the local cache.
   * Uses lazy initialization with synchronization.
   */
  private void ensureRatesLoaded() {
    if (ratesCache == null) {
      synchronized (this) {
        if (ratesCache == null) {
          ratesCache = loadRatesFromEcb();
        }
      }
    }
  }

  /**
   * Loads historical FX rates from the European Central Bank XML feed.
   * Parses daily currency rates into an in-memory cache.
   *
   * @return map of date to currency-rate mappings
   */
  private Map<LocalDate, Map<String, BigDecimal>> loadRatesFromEcb() {

    WebClient webClient = WebClient.builder()
            .exchangeStrategies(
                    ExchangeStrategies.builder()
                            .codecs(configurer ->
                                    configurer.defaultCodecs()
                                            .maxInMemorySize(20 * 1024 * 1024) // 20MB
                            )
                            .build()
            )
            .build();

    String xml = webClient.get()
            .uri(ECB_URL)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    if (xml == null) {
      throw new RuntimeException("Failed to fetch ECB FX data");
    }

    try {
      Document doc = DocumentBuilderFactory
              .newInstance()
              .newDocumentBuilder()
              .parse(new ByteArrayInputStream(
                      xml.getBytes(java.nio.charset.StandardCharsets.UTF_8)
              ));

      NodeList timeNodes =
              doc.getElementsByTagName("Cube");

      Map<LocalDate, Map<String, BigDecimal>> result =
              new HashMap<>();

      for (int i = 0; i < timeNodes.getLength(); i++) {

        Node node = timeNodes.item(i);

        if (node.getAttributes() != null
                && node.getAttributes().getNamedItem("time") != null) {

          LocalDate date = LocalDate.parse(
                  node.getAttributes()
                          .getNamedItem("time")
                          .getNodeValue());

          Map<String, BigDecimal> dayRates =
                  new HashMap<>();

          NodeList currencyNodes =
                  node.getChildNodes();

          for (int j = 0; j < currencyNodes.getLength(); j++) {

            Node currencyNode = currencyNodes.item(j);

            if (currencyNode.getAttributes() != null
                    && currencyNode.getAttributes()
                    .getNamedItem("currency") != null) {

              String curr =
                      currencyNode.getAttributes()
                              .getNamedItem("currency")
                              .getNodeValue();

              BigDecimal rate =
                      new BigDecimal(
                              currencyNode.getAttributes()
                                      .getNamedItem("rate")
                                      .getNodeValue());

              dayRates.put(curr, rate);
            }
          }

          result.put(date, dayRates);
        }
      }

      return result;

    } catch (Exception e) {
      throw new RuntimeException("Failed parsing ECB FX XML", e);
    }
  }

  /**
   * Finds the closest available FX rates on or before the given date.
   *
   * @param date target date for rate lookup
   * @return currency-rate mapping for the closest available date
   */
  private Map<String, BigDecimal> findClosestAvailableRates(
          LocalDate date) {

    LocalDate current = date;

    while (current.isAfter(LocalDate.of(1999, 1, 1))) {

      Map<String, BigDecimal> rates =
              ratesCache.get(current);

      if (rates != null) {
        return rates;
      }

      current = current.minusDays(1);
    }

    throw new RuntimeException(
            "No FX data available for date: " + date);
  }
}