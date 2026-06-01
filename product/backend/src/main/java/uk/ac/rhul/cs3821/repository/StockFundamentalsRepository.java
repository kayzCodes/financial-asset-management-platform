package uk.ac.rhul.cs3821.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs3821.entity.StockFundamentals;

/**
 * Repository interface for {@link StockFundamentals} entities.
 * Handles abstraction for CRUD operations and symbol-based lookups in the database.
 */
public interface StockFundamentalsRepository extends JpaRepository<StockFundamentals, Long> {

  /**
   * Finds the fundamental data for a specific stock identified by its ticker symbol.
   *
   * @param symbol the stock ticker symbol (e.g., "AAPL", "TSLA").
   * @return an Optional containing the stock fundamentals if present.
   */
  Optional<StockFundamentals> findBySymbol(String symbol);
}
