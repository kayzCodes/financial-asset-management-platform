package uk.ac.rhul.cs3821.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs3821.entity.CryptoFundamentals;

/**
 * Repository interface for {@link CryptoFundamentals} entities.
 * Provides abstraction for database operations including retrieval by ticker symbol.
 */
public interface CryptoFundamentalsRepository
        extends JpaRepository<CryptoFundamentals, Long> {

  /**
   * Retrieves cryptocurrency fundamental data based on its ticker symbol.
   *
   * @param symbol the ticker symbol (e.g., "BTC", "ETH").
   * @return an Optional containing the found fundamental data, or empty if not found.
   */
  Optional<CryptoFundamentals> findBySymbol(String symbol);
}
