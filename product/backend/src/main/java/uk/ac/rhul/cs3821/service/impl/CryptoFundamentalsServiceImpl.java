package uk.ac.rhul.cs3821.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.alphavantage.CryptoOverviewResponse;
import uk.ac.rhul.cs3821.dto.CryptoFundamentalsDto;
import uk.ac.rhul.cs3821.entity.CryptoFundamentals;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.mapper.CryptoFundamentalsMapper;
import uk.ac.rhul.cs3821.repository.CryptoFundamentalsRepository;
import uk.ac.rhul.cs3821.repository.UserCryptoRepository;
import uk.ac.rhul.cs3821.service.AlphaVantageService;
import uk.ac.rhul.cs3821.service.CryptoFundamentalsService;

/**
 * Implementation of the {@link CryptoFundamentalsService} providing
 * cached access and synchronization for cryptocurrency fundamental data.
 */
@Service
@RequiredArgsConstructor
public class CryptoFundamentalsServiceImpl
        implements CryptoFundamentalsService {

  private final CryptoFundamentalsRepository cryptoFundamentalsRepository;
  private final AlphaVantageService alphaVantageService;
  private final UserCryptoRepository userCryptoRepository;

  /**
   * Retrieves cryptocurrency fundamental data, prioritizing non-stale database records.
   * * If the database record is missing or stale, it fetches fresh data from the Alpha Vantage
   * API, updates the entity with safe defaults (guaranteeing a non-null name),
   * and persists the changes.
   *
   * @param symbol the asset ticker symbol.
   * @return the fundamental data DTO, or null if retrieval fails.
   */
  @Override
  public CryptoFundamentalsDto getCryptoFundamentals(String symbol) {

    // 1. Check DB first
    CryptoFundamentals existing =
            cryptoFundamentalsRepository.findBySymbol(symbol).orElse(null);

    if (existing != null && !isStale(existing)) {
      return CryptoFundamentalsMapper
              .mapToCryptoFundamentalsDto(existing);
    }

    // 2. Fetch from Alpha Vantage if missing or stale
    CryptoOverviewResponse overview =
            alphaVantageService.getCryptoOverview(symbol);

    if (overview == null) {
      return existing != null
              ? CryptoFundamentalsMapper
              .mapToCryptoFundamentalsDto(existing)
              : null;
    }

    // 3. Create or update entity
    CryptoFundamentals entity =
            existing != null ? existing : new CryptoFundamentals();

    entity.setSymbol(symbol);

    // GUARANTEE non-null name
    String name =
            overview.getName() != null && !overview.getName().isBlank()
                    ? overview.getName()
                    : symbol; // fallback

    entity.setName(name);

    entity.setDescription(overview.getDescription());


    if (overview.getMarketCap() != null) {
      try {
        entity.setMarketCap(
                new BigDecimal(overview.getMarketCap()));
      } catch (NumberFormatException ignored) {
        // keep existing value if malformed
      }
    }

    entity.setLastUpdatedAt(LocalDateTime.now());

    // 4. Persist
    CryptoFundamentals saved =
            cryptoFundamentalsRepository.save(entity);

    return CryptoFundamentalsMapper
            .mapToCryptoFundamentalsDto(saved);
  }

  /**
   * Crypto fundamentals are considered stale after 7 days.
   */
  private boolean isStale(CryptoFundamentals fundamentals) {
    return fundamentals.getLastUpdatedAt()
            .isBefore(LocalDateTime.now().minus(7, ChronoUnit.DAYS));
  }

  /**
   * Runs once per week at 03:00 (Monday).
   */
  @Generated
  @Scheduled(cron = "0 0 3 * * MON")
  @Override
  public void refreshAllFundamentals() {

    Set<String> symbols =
            userCryptoRepository.findAll().stream()
                    .map(UserCrypto::getSymbol)
                    .collect(Collectors.toSet());

    for (String symbol : symbols) {

      CryptoOverviewResponse overview =
              alphaVantageService.getCryptoOverview(symbol);

      if (overview == null) {
        continue; // rate limited or unavailable
      }

      CryptoFundamentals fundamentals =
              cryptoFundamentalsRepository.findBySymbol(symbol)
                      .orElse(new CryptoFundamentals());

      fundamentals.setSymbol(symbol);
      fundamentals.setName(overview.getName());
      fundamentals.setDescription(overview.getDescription());

      if (overview.getMarketCap() != null) {
        try {
          fundamentals.setMarketCap(
                  new BigDecimal(overview.getMarketCap()));
        } catch (NumberFormatException ignored) {
          // ignore invalid numeric format from API
        }
      }
      fundamentals.setLastUpdatedAt(LocalDateTime.now());

      cryptoFundamentalsRepository.save(fundamentals);
    }
  }
}
