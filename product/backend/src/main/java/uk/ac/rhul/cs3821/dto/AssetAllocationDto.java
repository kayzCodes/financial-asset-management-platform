package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;

/**
 * DTO representing a portfolio asset allocation entry.
 * Contains asset identity, value, allocation weight, and performance change.
 */
public record AssetAllocationDto(
        Long holdingId,
        String displayName,
        String assetType,
        BigDecimal valueGbp,
        BigDecimal allocationPercent,
        BigDecimal percentChange
) {
}