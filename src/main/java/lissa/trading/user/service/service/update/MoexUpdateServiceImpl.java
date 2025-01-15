package lissa.trading.user.service.service.update;

import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import lissa.trading.user.service.service.publisher.NotificationContext;
import lissa.trading.user.service.feign.moex.MoexServiceClient;
import lissa.trading.user.service.mapper.FavoriteStockMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.FavoriteStocksEntityRepository;
import lissa.trading.user.service.service.publisher.UserUpdatesPublisher;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoexUpdateServiceImpl implements UpdateService {

    private final MoexServiceClient moexServiceClient;
    private final FavoriteStockMapper favoriteStockMapper;
    private final FavoriteStocksEntityRepository favoriteStocksEntityRepository;
    private final UserRepository userRepository;
    private final NotificationContext notificationContext;
    private final UserUpdatesPublisher userUpdatesPublisher;

    private final static SupportedBrokersEnum BROKER = SupportedBrokersEnum.MOEX;

    @Override
    public SupportedBrokersEnum getBroker() {
        return BROKER;
    }

    @Override
    public void updateUserFavouriteStocks(User user, TickersDto tickersDto) {
        userRepository.save(user);
        log.info("updating favourite stocks for {} using {}", user, BROKER);
        List<FavoriteStocksEntity> favoriteStocks = user.getFavoriteStocks();
        log.info("Before {}", favoriteStocks);
        favoriteStocks.addAll(fetchFavouriteStocks(getTickersToAdd(favoriteStocks, tickersDto)));
        log.info("after {}", favoriteStocks);
        saveFavouriteStocks(favoriteStocks, user);
        log.info("saved favourite stocks for {}", user);
        user.setFavoriteStocks(favoriteStocks);
        userUpdatesPublisher.publishUserFavoriteStocksUpdateNotification(user);
    }

    @Override
    public StocksPricesDto getPricesUpdate(User user) {
        log.info("fetching favourite stock prices for {}", user);
        return moexServiceClient.getStocksPrices(getTickersDto(user.getFavoriteStocks()));
    }

    private TickersDto getTickersToAdd(List<FavoriteStocksEntity> existingStocks, TickersDto tickersDto) {
        List<String> existingStocksTickers = existingStocks
                .stream()
                .map(FavoriteStocksEntity::getTicker)
                .toList();
        tickersDto.getTickers().removeIf(existingStocksTickers::contains);
        return tickersDto;
    }

    private List<FavoriteStocksEntity> fetchFavouriteStocks(TickersDto tickersDto) {
         return favoriteStockMapper.toFavoriteStocksFromDto(
                 moexServiceClient.getStocksByTicker(tickersDto))
                 .stream()
                 .toList();
    }

    private void saveFavouriteStocks(List<FavoriteStocksEntity> favoriteStocksEntities, User user) {
        favoriteStocksEntities.stream().forEach(stock -> {
            stock.setUser(user);
            favoriteStocksEntityRepository.save(stock);
        });
    }
}
