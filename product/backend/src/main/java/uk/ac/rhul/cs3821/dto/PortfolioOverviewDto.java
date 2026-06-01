package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing a portfolio overview summary and asset allocation metrics.
 */
public record PortfolioOverviewDto(
        String baseCurrency,
        BigDecimal totalValueGbp,
        BigDecimal totalCostGbp,
        BigDecimal unrealisedPnlGbp,
        BigDecimal unrealisedPnlPercent,
        BigDecimal stocksPercent,
        BigDecimal cryptoPercent,
        List<AssetAllocationDto> assets,
        AssetAllocationDto topPerformer,
        AssetAllocationDto worstPerformer,
        List<ChartPointDto> chart,
        LocalDateTime lastUpdatedAt
) {
}