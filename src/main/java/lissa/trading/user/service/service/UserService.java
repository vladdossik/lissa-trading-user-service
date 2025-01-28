package lissa.trading.user.service.service;

import jakarta.validation.Valid;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.page.CustomPage;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto updateUser(UUID externalId, @Valid UserPatchDto userUpdates);

    void blockUserByTelegramNickname(String telegramNickname);

    void deleteUserByExternalId(UUID externalId);

    UserResponseDto getUserByExternalId(UUID externalId);

    CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable, String firstName, String lastName);

    List<UUID> getUserIdsWithPaginationAndFilters(Pageable pageable, String firstName,
                                                              String lastName);

    void updateFavoriteStocks(UUID externalId, TickersDto tickersDto);

    void setUpdatedFavoriteStocksToUser(UUID externalId, List<FavoriteStocksEntity> favoriteStocksEntities);

    StocksPricesDto getUpdateOnStockPrices(UUID externalId);
}