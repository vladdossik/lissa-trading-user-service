package lissa.trading.user.service.service;

import jakarta.validation.Valid;
import lissa.trading.user.service.dto.notification.OperationEnum;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.dto.tinkoff.stock.StockPrice;
import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import lissa.trading.user.service.exception.OperationUnsupportedByBrokerException;
import lissa.trading.user.service.mapper.FavoriteStockMapper;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.service.consumer.NotificationContext;
import lissa.trading.user.service.exception.UserNotFoundException;
import lissa.trading.user.service.mapper.PageMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.page.CustomPage;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.projections.UserExternalIdProjection;
import lissa.trading.user.service.service.publisher.UserUpdatesPublisher;
import lissa.trading.user.service.service.publisher.stats.StatsPublisher;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;
import lissa.trading.user.service.service.update.factory.UpdateServiceFactory;
import lissa.trading.user.service.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {

    private static final String STOCK_PRICES_KEY = "stockPrices";
    private static final String USERS_KEY = "users";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final StatsPublisher<User> statsPublisher;
    private final UpdateServiceFactory userUpdateServiceFactory;
    private final UserUpdatesPublisher userUpdatesPublisher;
    private final NotificationContext notificationContext;
    private final FavoriteStockMapper favoriteStockMapper;
    private final UpdateServiceFactory updateServiceFactory;
    private final RedisCacheManager redisCacheManager;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#externalId"),
            @CacheEvict(value = "usersPage", allEntries = true),
            @CacheEvict(value = "userIdsPage", allEntries = true)
    })
    @Transactional
    public UserResponseDto updateUser(UUID externalId, @Valid UserPatchDto userUpdates) {
        log.info("updating user with externalId {}", externalId);
        User user = userMapper.updateUserFromDto(userUpdates, findUserByExternalId(externalId));
        updateUserBroker(user);
        userRepository.save(user);
        log.info("updated user with externalId {}", externalId);
        userUpdatesPublisher.publishUserUpdateNotification(user, OperationEnum.UPDATE);
        notificationContext.clear();
        statsPublisher.publishUserData(user);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "usersPage", allEntries = true),
            @CacheEvict(value = "userIdsPage", allEntries = true)
    })
    @Transactional
    public void blockUserByTelegramNickname(String telegramNickname) {
        User user = findUserByTelegramNickname(telegramNickname);
        user.setIsMarginTradingEnabled(false);
        userRepository.save(user);
        log.info("User with Telegram nickname {} blocked", telegramNickname);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#externalId"),
            @CacheEvict(value = "usersPage", allEntries = true),
            @CacheEvict(value = "userIdsPage", allEntries = true)
    })
    @Transactional
    public void deleteUserByExternalId(UUID externalId) {
        log.info("deleting user {}", externalId);
        User user = findUserByExternalId(externalId);
        userRepository.delete(user);
        userUpdatesPublisher.publishUserUpdateNotification(user, OperationEnum.DELETE);
        notificationContext.clear();
        log.info("User with external ID {} deleted", externalId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByExternalId(UUID externalId) {
        return userMapper.toUserResponseDto(findUserByExternalId(externalId));
    }

    @Override
    @Cacheable(value = "usersPage",
            key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString(), #firstName, #lastName}")
    @Transactional(readOnly = true)
    public CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable, String firstName,
                                                                        String lastName) {
        log.info("Fetching users with pagination and filters - firstName: {}, lastName: {}", firstName, lastName);

        Specification<User> specification = UserSpecification.withFilters(firstName, lastName);
        Page<User> usersPage = userRepository.findAll(specification, pageable);

        CustomPage<UserResponseDto> customPage = PageMapper.toCustomPage(usersPage, userMapper::toUserResponseDto);

        log.info("Fetched {} users after pagination", customPage.getTotalElements());
        return customPage;
    }

    @Override
    @Cacheable(value = "userIdsPage",
            key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString(), #firstName, #lastName}")
    @Transactional(readOnly = true)
    public List<UUID> getUserIdsWithPaginationAndFilters(Pageable pageable, String firstName,
                                                                             String lastName) {
        log.info("Fetching user ids with pagination and filters - firstName: {}, lastName: {}", firstName, lastName);
        Specification<User> spec = UserSpecification.withFilters(firstName, lastName);
        Page<UserExternalIdProjection> usersPage = userRepository.findAll(spec, UserExternalIdProjection.class,
                pageable);

        return usersPage.getContent().stream()
                .map(UserExternalIdProjection::getExternalId)
                .toList();
    }

    @Override
    @Transactional
    public void updateFavoriteStocks(UUID externalId, TickersDto tickersDto) {
        log.info("trying to update stocks with dto {} for {}", tickersDto.getTickers(), externalId);
        User user = findUserByExternalId(externalId);
        userUpdateServiceFactory
                .getUpdateServiceByType(user.getBroker())
                .updateUserFavouriteStocks(user, tickersDto);
        userRepository.save(user);
        notificationContext.clear();
    }

    @Override
    @Transactional
    public void setUpdatedFavoriteStocksToUser(UUID externalId, List<FavoriteStocksEntity> favoriteStocksEntities) {
        User user = findUserByExternalId(externalId);
        try {
            List<FavoriteStocksEntity> existingStocks = user.getFavoriteStocks();

            Map<String, FavoriteStocksEntity> existingStocksMap = existingStocks.stream()
                    .collect(Collectors.toMap(FavoriteStocksEntity::getTicker, stock -> stock));

            List<FavoriteStocksEntity> updatedStocks = favoriteStocksEntities.stream()
                    .map(newStock -> {
                        FavoriteStocksEntity existingStock = existingStocksMap.get(newStock.getTicker());
                        if (existingStock != null) {
                            favoriteStockMapper.updateFavoriteStocksFromFavoriteStock(existingStock, newStock);
                            return existingStock;
                        } else {
                            newStock.setUser(user);
                            return newStock;
                        }
                    })
                    .toList();

            user.clearAndSetFavoriteStocks(updatedStocks);
            userRepository.save(user);

            log.info("User {} updated with {} favourite stocks",
                     user.getTelegramNickname(), favoriteStocksEntities.size());
        } catch (Exception e) {
            log.error("Failed to update user {} with new favourite stocks: {}",
                      user.getTelegramNickname(), e.getMessage(), e);
        }
    }

    @Override
    public StocksPricesDto getUpdateOnStockPrices(UUID externalId) {
        User user = findUserByExternalId(externalId);
        List<String> figies = user.getFavoriteStocks()
                .stream()
                .map(FavoriteStocksEntity::getFigi)
                .toList();

        List<StockPrice> stockPricesFromCache = getStockPricesFromCache(figies);
        if (stockPricesFromCache.size() == figies.size()) {
            return new StocksPricesDto(stockPricesFromCache);
        }
        StocksPricesDto stocksPricesFromApi = updateServiceFactory.getUpdateServiceByType(user.getBroker())
                .getPricesUpdate(user);
        stocksPricesFromApi.getPrices()
                .forEach(this::cacheStockPrice);
        return stocksPricesFromApi;
    }

    private void updateUserBroker(User user) {
        SupportedBrokersEnum broker = TokenUtils.determineTokenKind(user.getTinkoffToken());
        user.setBroker(broker);
        try {
            userUpdateServiceFactory
                    .getUpdateServiceByType(broker)
                    .userEntitiesUpdate(user);
        } catch(OperationUnsupportedByBrokerException e) {
            log.error("error while updating user broker, {}", e.getMessage());
        }
    }

    private User findUserByExternalId(UUID externalId) {
        Cache usersCache = redisCacheManager.getCache(USERS_KEY);
        User cachedUser = usersCache.get(externalId, User.class);
        if (cachedUser != null) {
            return cachedUser;
        }

        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException("User with external id " + externalId + " not found"));
        usersCache.put(externalId, user);
        return user;
    }

    private User findUserByTelegramNickname(String telegramNickname) {
        return userRepository.findByTelegramNickname(telegramNickname)
                .orElseThrow(() -> new UserNotFoundException("User with Telegram nickname " + telegramNickname + " not found"));
    }

    private List<StockPrice> getStockPricesFromCache(List<String> figies) {
        Cache stockPricesCache = redisCacheManager.getCache(STOCK_PRICES_KEY);
        return figies.stream()
                .map(figi -> stockPricesCache.get(figi, StockPrice.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void cacheStockPrice(StockPrice stockPrice) {
        redisCacheManager.getCache(STOCK_PRICES_KEY)
                .put(stockPrice.getFigi(), stockPrice);
    }
}