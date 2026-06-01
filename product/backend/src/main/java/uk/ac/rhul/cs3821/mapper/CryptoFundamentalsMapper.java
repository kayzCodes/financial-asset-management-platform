package uk.ac.rhul.cs3821.mapper;

import uk.ac.rhul.cs3821.dto.CryptoFundamentalsDto;
import uk.ac.rhul.cs3821.entity.CryptoFundamentals;

/**
 * Mapper class for CryptoFundamentals.
 */
public class CryptoFundamentalsMapper {

  /**
   * Maps a CryptoFundamentals entity to a CryptoFundamentalsDto.
   *
   * @param entity the CryptoFundamentals entity
   * @return the corresponding CryptoFundamentalsDto
   */
  public static CryptoFundamentalsDto mapToCryptoFundamentalsDto(
          CryptoFundamentals entity) {

    if (entity == null) {
      return null;
    }

    return new CryptoFundamentalsDto(
            entity.getId(),
            entity.getSymbol(),
            entity.getName(),
            entity.getMarketCap(),
            entity.getDescription(),
            entity.getLastUpdatedAt()
    );
  }

  /**
   * Maps a CryptoFundamentalsDto to a CryptoFundamentals entity.
   * IMPORTANT:
   * - Does NOT set createdAt / lastUpdatedAt
   * - These are managed by JPA lifecycle callbacks
   */
  public static CryptoFundamentals mapToCryptoFundamentals(
          CryptoFundamentalsDto dto) {

    if (dto == null) {
      return null;
    }

    CryptoFundamentals entity = new CryptoFundamentals();
    entity.setId(dto.getId());
    entity.setSymbol(dto.getSymbol());
    entity.setName(dto.getName());
    entity.setMarketCap(dto.getMarketCap());
    entity.setDescription(dto.getDescription());

    // DO NOT set timestamps here

    return entity;
  }
}
