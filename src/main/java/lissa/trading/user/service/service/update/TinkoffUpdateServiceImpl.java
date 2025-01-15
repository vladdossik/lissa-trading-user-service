package lissa.trading.user.service.service.update;

import lissa.trading.user.service.dto.tinkoff.UserAccount;
import lissa.trading.user.service.dto.tinkoff.account.BalanceDto;
import lissa.trading.user.service.dto.tinkoff.account.MarginAttributesDto;
import lissa.trading.user.service.dto.tinkoff.account.SecurityPosition;
import lissa.trading.user.service.dto.tinkoff.account.SecurityPositionsDto;
import lissa.trading.user.service.dto.tinkoff.account.TinkoffTokenDto;
import lissa.trading.user.service.dto.tinkoff.stock.FigiesDto;
import lissa.trading.user.service.dto.tinkoff.stock.StocksDto;
import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import lissa.trading.user.service.service.publisher.NotificationContext;
import lissa.trading.user.service.feign.tinkoff.TinkoffAccountClient;
import lissa.trading.user.service.mapper.FavoriteStockMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.BalanceEntity;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.model.entity.MarginTradingMetricsEntity;
import lissa.trading.user.service.model.entity.UserAccountEntity;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.BalanceEntityRepository;
import lissa.trading.user.service.repository.entity.FavoriteStocksEntityRepository;
import lissa.trading.user.service.repository.entity.MarginTradingMetricsEntityRepository;
import lissa.trading.user.service.repository.entity.UserAccountEntityRepository;
import lissa.trading.user.service.repository.entity.UserPositionsEntityRepository;
import lissa.trading.user.service.service.publisher.UserUpdatesPublisher;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TinkoffUpdateServiceImpl implements UpdateService {

    private final TinkoffAccountClient tinkoffAccountClient;
    private final BalanceEntityRepository balanceRepository;
    private final UserAccountEntityRepository userAccountRepository;
    private final MarginTradingMetricsEntityRepository marginTradingMetricsRepository;
    private final UserPositionsEntityRepository userPositionsRepository;
    private final FavoriteStocksEntityRepository favoriteStocksEntityRepository;
    private final FavoriteStockMapper favoriteStockMapper;
    private final UserUpdatesPublisher userUpdatesPublisher;
    private final NotificationContext notificationContext;

    private final static SupportedBrokersEnum BROKER = SupportedBrokersEnum.TINKOFF;
    private final UserRepository userRepository;

    @Override
    public SupportedBrokersEnum getBroker() {
        return BROKER;
    }

    public void fullUserUpdate(User user) {
        user.setAccountCount(updateUserAccounts(user));
        String accountId = getAccountId(user);
        updateUserBalance(user, accountId);
        updateUserMarginMetrics(user, accountId);
        updateUserFavouriteStocks(user, null);
        updateUserPositions(user, accountId);
        userRepository.save(user);
    }

    @Override
    public void updateUserBalance(User user, String accountId) {
        BalanceEntity newBalance = getBalanceFromTinkoff(accountId);
        Optional<BalanceEntity> existingBalanceOpt = user.getBalances().stream()
                .filter(balance -> balance.getCurrency().equals(newBalance.getCurrency()))
                .findFirst();

        if (existingBalanceOpt.isPresent()) {
            BalanceEntity existingBalance = existingBalanceOpt.get();
            existingBalance.setCurrentBalance(newBalance.getCurrentBalance());
            existingBalance.setTotalAmountBalance(newBalance.getTotalAmountBalance());
            balanceRepository.save(existingBalance);
        } else {
            newBalance.setUser(user);
            balanceRepository.save(newBalance);
            user.getBalances().add(newBalance);
        }
        log.info("User updated with balance: {}", user);
    }

    @Override
    public void updateUserMarginMetrics(User user, String accountId) {
        try {
            MarginAttributesDto marginAttributesDto = tinkoffAccountClient.getMarginAttributes(accountId);
            Optional<MarginTradingMetricsEntity> existingMetricsOpt = marginTradingMetricsRepository.findByUserId(user.getId());

            if (existingMetricsOpt.isPresent()) {
                MarginTradingMetricsEntity existingMetrics = existingMetricsOpt.get();
                existingMetrics.setCurrency(marginAttributesDto.getCurrency());
                existingMetrics.setLiquidPortfolio(marginAttributesDto.getLiquidPortfolio());
                marginTradingMetricsRepository.save(existingMetrics);
            } else {
                MarginTradingMetricsEntity newMetrics = new MarginTradingMetricsEntity();
                newMetrics.setUser(user);
                newMetrics.setCurrency(marginAttributesDto.getCurrency());
                newMetrics.setLiquidPortfolio(marginAttributesDto.getLiquidPortfolio());
                marginTradingMetricsRepository.save(newMetrics);
                user.setMarginTradingMetrics(newMetrics);
            }
            log.info("User updated with margin metrics: {}", user);
        } catch (Exception e) {
            log.error("Error updating margin metrics. Proceeding with empty metrics.", e);
        }
    }

    @Override
    public void updateUserPositions(User user, String accountId) {
        try {
            log.info("Updating user positions from Tinkoff API...");
            SecurityPositionsDto positionsDto = tinkoffAccountClient.getPositionsById(accountId);
            List<SecurityPosition> positionsFromApi = positionsDto.getPositions();
            List<UserPositionsEntity> existingPositions = user.getPositions();

            existingPositions.removeIf(existingPosition ->
                    positionsFromApi.stream().noneMatch(position ->
                            position.getFigi().equals(existingPosition.getFigi())));

            for (SecurityPosition position : positionsFromApi) {
                Optional<UserPositionsEntity> existingPositionOpt = existingPositions.stream()
                        .filter(pos -> pos.getFigi().equals(position.getFigi()))
                        .findFirst();

                if (existingPositionOpt.isPresent()) {
                    UserPositionsEntity existingPosition = existingPositionOpt.get();
                    existingPosition.setBalance(position.getBalance());
                    existingPosition.setBlocked(position.getBlocked());
                    userPositionsRepository.save(existingPosition);
                } else {
                    UserPositionsEntity newPosition = new UserPositionsEntity();
                    newPosition.setFigi(position.getFigi());
                    newPosition.setBalance(position.getBalance());
                    newPosition.setBlocked(position.getBlocked());
                    newPosition.setUser(user);
                    userPositionsRepository.save(newPosition);
                    user.getPositions().add(newPosition);
                }
            }
            log.info("User updated with positions: {}", user);
        } catch (Exception e) {
            log.warn("No positions found for user: {}", user.getId(), e);
        }
    }

    @Override
    public int updateUserAccounts(User user) {
        List<UserAccount> accountsFromApi = tinkoffAccountClient.getAccountsInfo().getUserAccounts();
        int updatedAccountCount = 0;

        for (UserAccount account : accountsFromApi) {
            Optional<UserAccountEntity> existingAccountOpt = userAccountRepository.findByAccountId(account.getAccountId());

            if (existingAccountOpt.isPresent()) {
                UserAccountEntity existingAccount = existingAccountOpt.get();
                existingAccount.setTariff(account.getTariff());
                existingAccount.setPremStatus(account.isPremStatus());
                userAccountRepository.save(existingAccount);
            } else {
                UserAccountEntity newAccount = new UserAccountEntity();
                newAccount.setAccountId(account.getAccountId());
                newAccount.setTariff(account.getTariff());
                newAccount.setPremStatus(account.isPremStatus());
                newAccount.setUser(user);
                userAccountRepository.save(newAccount);
                user.getUserAccounts().add(newAccount);
            }
            updatedAccountCount++;
        }
        log.info("Updated account count: {}", updatedAccountCount);
        return updatedAccountCount;
    }

    @Override
    public void updateUserFavouriteStocks(User user, TickersDto tickersDto) {
        try {
            userRepository.save(user);
            tinkoffAccountClient.setTinkoffToken(new TinkoffTokenDto(user.getTinkoffToken()));
            List<String> favouriteStocksFromApi = tinkoffAccountClient.getFavouriteStocks()
                    .getFavouriteStocks();
            List<FavoriteStocksEntity> existingFavouriteStocks = user.getFavoriteStocks();
            log.info("recieved existent favorite stocks {}", existingFavouriteStocks);
            List<FavoriteStocksEntity> fetchedFavouriteStocks = fetchFavouriteStocks(new TickersDto(
                    getTickersToAdd(existingFavouriteStocks, favouriteStocksFromApi)));
            log.info("fetched favorite stocks to add {}", fetchedFavouriteStocks);
            existingFavouriteStocks.addAll(fetchedFavouriteStocks);
            updateStockInformation(existingFavouriteStocks);
            log.info("updated stock information {}", existingFavouriteStocks);
            saveFavouriteStocks(existingFavouriteStocks, user);
            log.info("saved favourite stocks for {}", user);
            user.setFavoriteStocks(existingFavouriteStocks);
            userUpdatesPublisher.publishUserFavoriteStocksUpdateNotification(user);
        }
        catch (Exception e) {
            log.error("Error updating favourite stocks from Tinkoff API.", e);
        }
    }

    @Override
    public StocksPricesDto getPricesUpdate(User user) {
        log.info("fetching favorite stock prices for: {}", user);
        return tinkoffAccountClient
                .getPricesStocksByFigies(new FigiesDto(
                        user.getFavoriteStocks()
                                .stream()
                                .map(FavoriteStocksEntity::getFigi)
                                .toList()));
    }

    private List<String> getTickersToAdd(List<FavoriteStocksEntity> existingFavouriteStocks,
                                       List<String> favouriteStocksFromApi) {
        existingFavouriteStocks.removeIf(stock -> !stock
                .getServiceTicker()
                .equals(BROKER.name()) && !favouriteStocksFromApi.contains(stock.getTicker()));
        Set<String> existingTickers = existingFavouriteStocks.stream()
                .map(FavoriteStocksEntity::getTicker)
                .collect(Collectors.toSet());
        return favouriteStocksFromApi
                .stream()
                .filter(ticker -> !existingTickers
                        .contains(ticker))
                .toList();
    }

    private List<FavoriteStocksEntity> fetchFavouriteStocks(TickersDto tickersDto) {
        return favoriteStockMapper
                .toFavoriteStocksFromDto(tinkoffAccountClient.getStocksByTickers(tickersDto))
                .stream()
                .toList();
    }

    private void updateStockInformation(List<FavoriteStocksEntity> favoriteStocksEntities) {
        StocksDto stocksDto = tinkoffAccountClient.getStocksByTickers(getTickersDto(favoriteStocksEntities));
        favoriteStockMapper.updateFavoriteStocksFromDto(stocksDto, favoriteStocksEntities);
    }

    private void saveFavouriteStocks(List<FavoriteStocksEntity> favoriteStocksEntities, User user) {
        favoriteStocksEntities.stream().forEach(stock -> {
            stock.setUser(user);
            favoriteStocksEntityRepository.save(stock);
        });
    }

    private BalanceEntity getBalanceFromTinkoff(String tinkoffAccountId) {
        BalanceDto portfolio = tinkoffAccountClient.getPortfolio(tinkoffAccountId);
        return new BalanceEntity(
                null,
                null,
                portfolio.getCurrency(),
                portfolio.getCurrentBalance(),
                portfolio.getTotalAmountBalance()
        );
    }

}
