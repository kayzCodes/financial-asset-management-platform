package uk.ac.rhul.cs3821.mapper;

import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserStockDto;
import uk.ac.rhul.cs3821.entity.UserStock;

/**
 * This is the mapper class for UserStock.
 */
public class UserStockMapper {

  /**
   * Maps a UserStock entity to a UserStockDto.
   *
   * @param userStock the UserStock entity.
   * @return the corresponding UserStockDto.
   */
  public static UserStockDto mapToUserStockDto(UserStock userStock) {
    if (userStock == null) {
      return null;
    }

    UserDto userDto = UserMapper.mapToUserDto(userStock.getUser());

    return new UserStockDto(
            userStock.getHoldingId(),
            userDto,
            userStock.getTickerSymbol(),
            userStock.getCompanyName(),
            userStock.getQuantity(),
            userStock.getCurrency(),
            userStock.getAveragePurchasePrice(),
            userStock.getLastTransactionAt(),
            userStock.getLastUpdatedPriceAt(),
            userStock.getIsDeleted(),
            userStock.getNotes(),
            userStock.getCreatedAt(),
            userStock.getUpdatedAt()
    );
  }

  /**
   * Maps a UserStockDto to a UserStock entity.
   *
   * @param dto the UserStockDto.
   * @return the corresponding UserStock entity.
   */
  public static UserStock mapToUserStock(UserStockDto dto) {
    if (dto == null) {
      return null;
    }

    UserStock userStock = new UserStock();
    userStock.setHoldingId(dto.getHoldingId());

    if (dto.getUser() != null) {
      userStock.setUser(UserMapper.mapToUser(dto.getUser()));
    }

    userStock.setTickerSymbol(dto.getTickerSymbol());
    userStock.setCompanyName(dto.getCompanyName());
    userStock.setQuantity(dto.getQuantity());
    userStock.setCurrency(dto.getCurrency());
    userStock.setAveragePurchasePrice(dto.getAveragePurchasePrice());
    userStock.setLastTransactionAt(dto.getLastTransactionAt());
    userStock.setLastUpdatedPriceAt(dto.getLastUpdatedPriceAt());
    userStock.setIsDeleted(dto.getIsDeleted());
    userStock.setNotes(dto.getNotes());
    userStock.setCreatedAt(dto.getCreatedAt());
    userStock.setUpdatedAt(dto.getUpdatedAt());

    return userStock;
  }
}
