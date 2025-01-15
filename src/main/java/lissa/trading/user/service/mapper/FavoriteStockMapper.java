package lissa.trading.user.service.mapper;

import lissa.trading.user.service.dto.tinkoff.Stock;
import lissa.trading.user.service.dto.tinkoff.stock.StocksDto;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FavoriteStockMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "source", target = "serviceTicker")
    @Mapping(source = "type", target = "instrumentType")
    FavoriteStocksEntity toFavoriteStock(Stock stock);

    List<FavoriteStocksEntity> toFavoriteStocksFromStocks(List<Stock> stocks);

    default List<FavoriteStocksEntity> toFavoriteStocksFromDto(StocksDto stocksDto) {
        return toFavoriteStocksFromStocks(stocksDto.getStocks());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(source = "source", target = "serviceTicker")
    void updateFavoriteStockFromStockEntity(Stock stock, @MappingTarget FavoriteStocksEntity favoriteStocksEntity);

    @IterableMapping(elementTargetType = FavoriteStocksEntity.class)
    default void updateFavoriteStocksFromDto(StocksDto stocksDto, List<FavoriteStocksEntity> favoriteStocks) {
        if (stocksDto.getStocks() != null) {
            for (Stock stock : stocksDto.getStocks()) {
                favoriteStocks.stream()
                        .filter(entity -> entity.getTicker().equals(stock.getTicker()))
                        .findFirst()
                        .ifPresent(entity -> updateFavoriteStockFromStockEntity(stock, entity));
            }
        }
    }
}
