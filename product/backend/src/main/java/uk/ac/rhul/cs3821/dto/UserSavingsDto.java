package uk.ac.rhul.cs3821.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing a user saving transaction entry.
 */
public record UserSavingsDto(
        Long id,
        Long goalId,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}