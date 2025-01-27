package lissa.trading.user.service.service.update;

import lissa.trading.user.service.dto.tinkoff.Stock;
import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import lissa.trading.user.service.feign.moex.MoexServiceClient;
import lissa.trading.user.service.mapper.FavoriteStockMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.service.publisher.UserUpdatesPublisher;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoexUpdateServiceImpl implements UpdateService {

    private final static SupportedBrokersEnum BROKER = SupportedBrokersEnum.MOEX;

    private final MoexServiceClient moexServiceClient;
    private final FavoriteStockMapper favoriteStockMapper;
    private final UserRepository userRepository;
    private final UserUpdatesPublisher userUpdatesPublisher;

    @Override
    public SupportedBrokersEnum getBroker() {
        return BROKER;
    }

    @Override
    public void updateUserFavouriteStocks(User user, TickersDto tickersDto) {
        if (CollectionUtils.isEmpty(tickersDto.getTickers())) {
            updateStockInformation(user.getFavoriteStocks());
            userRepository.save(user);
            userUpdatesPublisher.publishUserFavoriteStocksUpdateNotification(user);
            return;
        }
        log.info("updating favourite stocks for {} using {}", user.getExternalId(), BROKER);
        List<FavoriteStocksEntity> favoriteStocks = user.getFavoriteStocks();
        favoriteStocks.addAll(favoriteStockMapper.toFavoriteStocksFromStocks(
                        fetchStocks(getTickersToAdd(favoriteStocks, tickersDto))));
        updateStockInformation(favoriteStocks);
        favoriteStocks.forEach(stock -> stock.setUser(user));
        user.setFavoriteStocks(favoriteStocks);
        userRepository.save(user);
        userUpdatesPublisher.publishUserFavoriteStocksUpdateNotification(user);
        log.info("successfully updated user favorite stocks {}", favoriteStocks);
    }

    @Override
    public StocksPricesDto getPricesUpdate(User user) {
        log.info("fetching favourite stock prices for {}", user);
        return moexServiceClient.getStocksPrices(getTickersDto(filterFavoriteStocksByBroker(
                user.getFavoriteStocks())));
    }

    private TickersDto getTickersToAdd(List<FavoriteStocksEntity> existingStocks, TickersDto tickersDto) {
        Set<String> existingStocksTickers = existingStocks
                .stream()
                .map(FavoriteStocksEntity::getTicker)
                .collect(Collectors.toSet());

        List<String> filteredTickers = tickersDto.getTickers()
                .stream()
                .filter(ticker -> !existingStocksTickers.contains(ticker))
                .toList();
        return new TickersDto(filteredTickers);
    }

    private void updateStockInformation(List<FavoriteStocksEntity> favoriteStocksEntities) {
        if (favoriteStocksEntities.isEmpty()) {
            return;
        }
        List<FavoriteStocksEntity> filteredList = filterFavoriteStocksByBroker(favoriteStocksEntities);
        if (filteredList.isEmpty()) {
            return;
        }
        List<Stock> fetchedStocks = fetchStocks(getTickersDto(filteredList));
        Map<String, Stock> stockDtoMap = fetchedStocks.stream()
                .collect(Collectors.toMap(Stock::getTicker, stock -> stock));

        for (FavoriteStocksEntity favoriteStock : favoriteStocksEntities) {
            if (favoriteStock.getServiceTicker().equals(getBroker().name())) {
                if (stockDtoMap.get(favoriteStock.getTicker()) != null) {
                    favoriteStockMapper.updateFavoriteStockFromStock(
                            stockDtoMap.get(favoriteStock.getTicker()), favoriteStock);
                }
            }
        }
    }

    private List<Stock> fetchStocks(TickersDto tickersDto) {
        log.info("trying to fetch favourite stocks with dto: {}", tickersDto);
        return moexServiceClient.getStocksByTicker(tickersDto)
                .getStocks()
                .stream()
                .toList();
    }
}
