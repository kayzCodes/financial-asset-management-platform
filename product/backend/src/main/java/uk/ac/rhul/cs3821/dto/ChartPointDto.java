package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing a single data point on a price chart.
 * Pairs a specific date with its corresponding closing price.
 *
 * @param date  the timestamp or date string for the data point.
 * @param close the closing price of the asset at the given date.
 */
public record ChartPointDto(
        String date,
        BigDecimal close
) {
}

