package uk.ac.rhul.cs3821.records;

import java.math.BigDecimal;

/**
 * Record representing valuation metrics for a portfolio holding.
 */
public record HoldingValuation(
        Long holdingId,
        String displayName,
        String assetType,
        BigDecimal quantityHeld,
        BigDecimal costBasisGbp,
        BigDecimal currentValueGbp,
        BigDecimal unrealisedPnlGbp,
        BigDecimal unrealisedPnlPercent,
        BigDecimal realisedPnlGbp
) {
}