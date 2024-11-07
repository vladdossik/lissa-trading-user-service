package lissa.trading.user.service.service.creation;

import lissa.trading.user.service.dto.tinkoff.UserAccount;
import lissa.trading.user.service.dto.tinkoff.account.BalanceDto;
import lissa.trading.user.service.dto.tinkoff.account.FavouriteStocksDto;
import lissa.trading.user.service.dto.tinkoff.account.MarginAttributesDto;
import lissa.trading.user.service.dto.tinkoff.account.SecurityPosition;
import lissa.trading.user.service.dto.tinkoff.account.SecurityPositionsDto;
import lissa.trading.user.service.dto.tinkoff.account.TinkoffTokenDto;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.feign.TinkoffAccountClient;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.BalanceEntity;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.model.entity.MarginTradingMetricsEntity;
import lissa.trading.user.service.model.entity.UserAccountEntity;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.BalanceEntityRepository;
import lissa.trading.user.service.repository.entity.FavoriteStocksEntityRepository;
import lissa.trading.user.service.repository.entity.MarginTradingMetricsEntityRepository;
import lissa.trading.user.service.repository.entity.UserAccountEntityRepository;
import lissa.trading.user.service.repository.entity.UserPositionsEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreationServiceImpl implements UserCreationService {

    private final UserRepository userRepository;
    private final TempUserRegRepository tempUserRegRepository;
    private final FavoriteStocksEntityRepository favoriteStocksRepository;
    private final UserPositionsEntityRepository userPositionsRepository;
    private final MarginTradingMetricsEntityRepository marginTradingMetricsRepository;
    private final TinkoffAccountClient tinkoffAccountClient;
    private final BalanceEntityRepository balanceRepository;
    private final UserAccountEntityRepository userAccountRepository;
    private final UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createUserFromTempUserReg(TempUserReg tempUserReg) {
        try {
            log.info("Starting to create user from TempUserReg: {}", tempUserReg);

            User user = userMapper.toUserFromTempUserReg(tempUserReg);
            tinkoffAccountClient.setTinkoffToken(new TinkoffTokenDto(tempUserReg.getTinkoffToken()));

            getTelegramInfo(user);

            String tinkoffId = getTinkoffAccountId();
            updateUserFromTinkoffData(user, tinkoffId);

            userRepository.save(user);
            tempUserRegRepository.delete(tempUserReg);

            log.info("User created and saved successfully: {}", user);

        } catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException while creating user from TempUserReg: {}", tempUserReg, e);
            throw new UserCreationException("Table error creating user from TempUserReg: " + e.getMessage());
        } catch (Exception e) {
            log.error("Exception while creating user from TempUserReg: {}", tempUserReg, e);
            throw new UserCreationException("Error creating user from TempUserReg", e);
        }
    }

    private String getTinkoffAccountId() {
        try {
            return tinkoffAccountClient.getAccountsInfo().getUserAccounts().get(0).getAccountId();
        } catch (Exception e) {
            log.error("Failed to get Tinkoff account ID.", e);
            throw new UserCreationException("Error fetching Tinkoff account ID.", e);
        }
    }

    private void updateUserFromTinkoffData(User user, String tinkoffAccountId) {
        userRepository.save(user);
        updateUserBalance(user, tinkoffAccountId);
        user.setAccountCount(updateUserAccounts(user));
        updateUserMarginMetrics(user, tinkoffAccountId);
        log.info("User updated with margin metrics: {}", user);
        updateUserFavouriteStocks(user);
        log.info("User updated with favourite stocks: {}", user);
        updateUserPositions(user, tinkoffAccountId);
        log.info("User updated with positions: {}", user);
    }

    private void updateUserBalance(User user, String tinkoffAccountId) {
        BalanceEntity newBalance = getBalanceFromTinkoff(tinkoffAccountId);
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

    private int updateUserAccounts(User user) {
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

    private void updateUserMarginMetrics(User user, String tinkoffAccountId) {
        try {
            MarginAttributesDto marginAttributesDto = tinkoffAccountClient.getMarginAttributes(tinkoffAccountId);

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
        } catch (Exception e) {
            log.error("Error updating margin metrics. Proceeding with empty metrics.", e);
        }
    }

    private void updateUserFavouriteStocks(User user) {
        try {
            FavouriteStocksDto favouriteStocksDto = tinkoffAccountClient.getFavouriteStocks();
            List<String> favouriteStocksFromApi = favouriteStocksDto.getFavouriteStocks();
            List<FavoriteStocksEntity> existingFavouriteStocks = user.getFavoriteStocks();

            existingFavouriteStocks.removeIf(existingStock ->
                    !favouriteStocksFromApi.contains(existingStock.getStockName()));

            for (String stock : favouriteStocksFromApi) {
                Optional<FavoriteStocksEntity> existingStockOpt = existingFavouriteStocks.stream()
                        .filter(favStock -> favStock.getStockName().equals(stock))
                        .findFirst();

                if (existingStockOpt.isEmpty()) {
                    FavoriteStocksEntity newStock = new FavoriteStocksEntity();
                    newStock.setStockName(stock);
                    newStock.setUser(user);
                    favoriteStocksRepository.save(newStock);
                    user.getFavoriteStocks().add(newStock);
                }
            }
        } catch (Exception e) {
            log.warn("No favourite stocks found for user: {}", user.getId(), e);
        }
    }

    private void updateUserPositions(User user, String tinkoffAccountId) {
        try {
            log.info("Updating user positions from Tinkoff API...");
            SecurityPositionsDto positionsDto = tinkoffAccountClient.getPositionsById(tinkoffAccountId);
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
        } catch (Exception e) {
            log.warn("No positions found for user: {}", user.getId(), e);
        }
    }

    private void getTelegramInfo(User user) {
        if (user.getTelegramChatId() == null) {
            user.setTelegramChatId(ThreadLocalRandom.current().nextLong());
        }
    }
}
