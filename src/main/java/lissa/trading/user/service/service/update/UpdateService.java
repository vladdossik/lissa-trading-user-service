package lissa.trading.user.service.service.update;

import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import lissa.trading.user.service.exception.OperationUnsupportedByBrokerException;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;

import java.util.List;

public interface UpdateService {
    default void userEntitiesUpdate(User user) {
        throw new OperationUnsupportedByBrokerException("User entities update operation unsupported by: "
                + getBroker());
    }

    default void updateUserFavouriteStocks(User user, TickersDto tickersDto) {
        throw new OperationUnsupportedByBrokerException("Favourite stocks update operation unsupported by: "
                                                                + getBroker());
    }

    default StocksPricesDto getPricesUpdate(User user) {
        throw new OperationUnsupportedByBrokerException("Favourite stock prices update operation unsupported by: "
                                                                + getBroker());
    }

    default void updateUserBalance(User user, String accountId) {
        throw new OperationUnsupportedByBrokerException("User balance update operation unsupported by: "
                + getBroker());
    }

    default void updateUserMarginMetrics(User user, String accountId) {
        throw new OperationUnsupportedByBrokerException("User margin metrics update operation unsupported by: "
                + getBroker());
    }

    default void updateUserPositions(User user, String accountId) {
        throw new OperationUnsupportedByBrokerException("User positions update operation unsupported by: "
                + getBroker());
    }

    default int updateUserAccounts(User user) {
        throw new OperationUnsupportedByBrokerException("User accounts update operation unsupported by: "
                + getBroker());
    }

    default String getAccountId(User user) {
        try {
            return user.getUserAccounts().get(0).getAccountId();
        } catch (Exception e) {
            throw new UserCreationException("Error fetching User account ID.", e);
        }
    }

    default TickersDto getTickersDto(List<FavoriteStocksEntity> favoriteStocksEntities) {
        return new TickersDto(favoriteStocksEntities
                                      .stream()
                                      .map(FavoriteStocksEntity::getTicker)
                                      .toList());
    }

    default List<FavoriteStocksEntity> filterFavoriteStocksByBroker(
            List<FavoriteStocksEntity> favoriteStocksEntities) {
        return favoriteStocksEntities.stream()
                .filter(stock -> stock.getServiceTicker().equals(getBroker().name()))
                .toList();
    }

    SupportedBrokersEnum getBroker();
}
