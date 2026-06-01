package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;

/**
 * DTO representing a request to add funds to a saving goal.
 */
public record AddSavingRequestDto(
        Long goalId,
        BigDecimal amount
) {
}