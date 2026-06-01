package uk.ac.rhul.cs3821.dto;

/**
 * DTO for creating a new crypto holding with its initial buy transaction.
 */
public record AddCryptoRequestDto(
        UserCryptoDto holding,
        CryptoTransactionRequestDto initialBuy
) {
}