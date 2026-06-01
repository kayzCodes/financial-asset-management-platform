package uk.ac.rhul.cs3821.records;

import java.math.BigDecimal;

/**
 * Record representing calculated holding cost metrics.
 */
public record HoldingCostResult(
        BigDecimal quantityHeld,
        BigDecimal costBasisGbp,
        BigDecimal realisedPnlGbp
) {
}
