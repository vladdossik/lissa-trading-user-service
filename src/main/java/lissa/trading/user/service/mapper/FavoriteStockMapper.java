package lissa.trading.user.service.mapper;

import lissa.trading.user.service.dto.notification.NotificationFavouriteStockDto;
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

    FavoriteStocksEntity toFavoriteStockFromNotificationFavoriteStockDto(
            NotificationFavouriteStockDto favouriteStocksDto);

    List<FavoriteStocksEntity> toFavoriteStocksFromNotificationFavoriteStockDto(
            List<NotificationFavouriteStockDto> favouriteStockDtoList);

    List<FavoriteStocksEntity> toFavoriteStocksFromStocks(List<Stock> stocks);

    NotificationFavouriteStockDto toNotificationFavouriteStockDto(FavoriteStocksEntity favoriteStocksEntity);

    List<NotificationFavouriteStockDto> toNotificationFavouriteStockDtoList(List<FavoriteStocksEntity> favoriteStocksEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "serviceTicker", ignore = true)
    void updateFavoriteStockFromStock(Stock stock, @MappingTarget FavoriteStocksEntity favoriteStocksEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "serviceTicker", ignore = true)
    void updateFavoriteStocksFromFavoriteStock(FavoriteStocksEntity favoriteStocksEntity,
                                               @MappingTarget FavoriteStocksEntity targetFavoriteStocksEntity);

}
