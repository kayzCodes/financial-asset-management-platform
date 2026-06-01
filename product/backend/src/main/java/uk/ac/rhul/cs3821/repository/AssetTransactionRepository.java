package uk.ac.rhul.cs3821.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.rhul.cs3821.entity.AssetTransaction;
import uk.ac.rhul.cs3821.entity.UserCrypto;
import uk.ac.rhul.cs3821.entity.UserStock;

/**
 * Repository for accessing asset transaction records.
 * Supports queries for stock and crypto transactions by user and holding.
 */
@Repository
public interface AssetTransactionRepository
        extends JpaRepository<AssetTransaction, Long> {

  List<AssetTransaction> findByUser_IdAndStockHoldingOrderByOccurredAtAsc(
          Long userId,
          UserStock stockHolding
  );

  List<AssetTransaction> findByUser_IdAndCryptoHoldingOrderByOccurredAtAsc(
          Long userId,
          UserCrypto cryptoHolding
  );
}