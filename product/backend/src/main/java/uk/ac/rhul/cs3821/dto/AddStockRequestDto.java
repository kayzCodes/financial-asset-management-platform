package uk.ac.rhul.cs3821.dto;

/**
 * DTO for creating a new stock holding with its initial buy transaction.
 */
public record AddStockRequestDto(
        UserStockDto holding,
        StockTransactionRequestDto initialBuy
) {
}