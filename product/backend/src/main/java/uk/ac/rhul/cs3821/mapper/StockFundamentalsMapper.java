package uk.ac.rhul.cs3821.mapper;

import uk.ac.rhul.cs3821.dto.StockFundamentalsDto;
import uk.ac.rhul.cs3821.entity.StockFundamentals;

/**
 * Mapper class for StockFundamentals.
 */
public class StockFundamentalsMapper {

  /**
   * Maps a StockFundamentals entity to a StockFundamentalsDto.
   *
   * @param entity the StockFundamentals entity
   * @return the corresponding StockFundamentalsDto
   */
  public static StockFundamentalsDto mapToStockFundamentalsDto(StockFundamentals entity) {
    if (entity == null) {
      return null;
    }

    return new StockFundamentalsDto(
            entity.getId(),
            entity.getSymbol(),
            entity.getMarketCap(),
            entity.getPeRatio(),
            entity.getEps(),
            entity.getSector(),
            entity.getIndustry(),
            entity.getDescription(),
            entity.getLastUpdatedAt()
    );
  }

  /**
   * Maps a StockFundamentalsDto to a StockFundamentals entity.
   *
   * @param dto the StockFundamentalsDto
   * @return the corresponding StockFundamentals entity
   */
  public static StockFundamentals mapToStockFundamentals(StockFundamentalsDto dto) {
    if (dto == null) {
      return null;
    }

    StockFundamentals entity = new StockFundamentals();
    entity.setId(dto.getId());
    entity.setSymbol(dto.getSymbol());
    entity.setMarketCap(dto.getMarketCap());
    entity.setPeRatio(dto.getPeRatio());
    entity.setEps(dto.getEps());
    entity.setSector(dto.getSector());
    entity.setIndustry(dto.getIndustry());
    entity.setDescription(dto.getDescription());
    entity.setLastUpdatedAt(dto.getLastUpdatedAt());

    return entity;
  }
}
