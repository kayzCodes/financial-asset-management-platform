package uk.ac.rhul.cs3821.mapper;

import uk.ac.rhul.cs3821.dto.UserCryptoDto;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.entity.UserCrypto;

/**
 * This is the mapper class for UserCrypto.
 */
public class UserCryptoMapper {

  /**
   * Maps a UserCrypto entity to a UserCryptoDto.
   *
   * @param userCrypto the UserCrypto entity.
   * @return the corresponding UserCryptoDto.
   */
  public static UserCryptoDto mapToUserCryptoDto(UserCrypto userCrypto) {
    if (userCrypto == null) {
      return null;
    }

    UserDto userDto = UserMapper.mapToUserDto(userCrypto.getUser());

    return new UserCryptoDto(
            userCrypto.getHoldingId(),
            userDto,
            userCrypto.getSymbol(),
            userCrypto.getName(),
            userCrypto.getQuantity(),
            userCrypto.getCurrency(),
            userCrypto.getAveragePurchasePrice(),
            userCrypto.getLastTransactionAt(),
            userCrypto.getLastUpdatedPriceAt(),
            userCrypto.getIsDeleted(),
            userCrypto.getNotes(),
            userCrypto.getCreatedAt(),
            userCrypto.getUpdatedAt()
    );
  }

  /**
   * Maps a UserCryptoDto to a UserCrypto entity.
   *
   * @param dto the UserCryptoDto.
   * @return the corresponding UserCrypto entity.
   */
  public static UserCrypto mapToUserCrypto(UserCryptoDto dto) {
    if (dto == null) {
      return null;
    }

    UserCrypto userCrypto = new UserCrypto();
    userCrypto.setHoldingId(dto.getHoldingId());

    // Only map the user if it's present in the DTO
    if (dto.getUser() != null) {
      userCrypto.setUser(UserMapper.mapToUser(dto.getUser()));
    }

    userCrypto.setSymbol(dto.getSymbol());
    userCrypto.setName(dto.getName());
    userCrypto.setQuantity(dto.getQuantity());
    userCrypto.setCurrency(dto.getCurrency());
    userCrypto.setAveragePurchasePrice(dto.getAveragePurchasePrice());
    userCrypto.setLastTransactionAt(dto.getLastTransactionAt());
    userCrypto.setLastUpdatedPriceAt(dto.getLastUpdatedPriceAt());
    userCrypto.setIsDeleted(dto.getIsDeleted());
    userCrypto.setNotes(dto.getNotes());
    userCrypto.setCreatedAt(dto.getCreatedAt());
    userCrypto.setUpdatedAt(dto.getUpdatedAt());

    return userCrypto;
  }
}
