package lissa.trading.user.service.service;

import jakarta.validation.Valid;
import lissa.trading.user.service.dto.notification.OperationEnum;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import lissa.trading.user.service.service.publisher.NotificationContext;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final StatsPublisher<User> statsPublisher;
    private final UpdateServiceFactory userUpdateServiceFactory;
    private final UserUpdatesPublisher userUpdatesPublisher;
    private final NotificationContext notificationContext;

    @Override
    @Transactional
    public UserResponseDto updateUser(UUID externalId, @Valid UserPatchDto userUpdates) {
        log.info("updating user {}", externalId);
        User user = userMapper.updateUserFromDto(userUpdates, findUserByExternalId(externalId));
        updateUserBroker(user);
        userRepository.save(user);
        log.info("updated user {}", user);
        userUpdatesPublisher.publishUserUpdateNotification(user, OperationEnum.UPDATE);
        notificationContext.clear();
        statsPublisher.publishUserData(user);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public void blockUserByTelegramNickname(String telegramNickname) {
        User user = findUserByTelegramNickname(telegramNickname);
        user.setIsMarginTradingEnabled(false);
        userRepository.save(user);
        log.info("User with Telegram nickname {} blocked", telegramNickname);
    }

    @Override
    @Transactional
    public void deleteUserByExternalId(UUID externalId) {
        User deletedUser = findUserByExternalId(externalId);
        log.info("deleting user {}", deletedUser);
        userRepository.delete(deletedUser);
        userUpdatesPublisher.publishUserUpdateNotification(deletedUser, OperationEnum.DELETE);
        notificationContext.clear();
        log.info("User with external ID {} deleted", externalId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByExternalId(UUID externalId) {
        return userMapper.toUserResponseDto(findUserByExternalId(externalId));
    }

    @Override
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
        User user = findUserByExternalId(externalId);
        userUpdateServiceFactory
                .getUpdateServiceByType(user.getBroker())
                .updateUserFavouriteStocks(user, tickersDto);
        userRepository.save(user);
        notificationContext.clear();
    }

    @Override
    public StocksPricesDto getUpdateOnStockPrices(UUID externalId) {
        User user = findUserByExternalId(externalId);
        return userUpdateServiceFactory
                .getUpdateServiceByType(user.getBroker())
                .getPricesUpdate(user);
    }

    private void updateUserBroker(User user) {
        String tinkoffToken = user.getTinkoffToken();
        if (tinkoffToken.startsWith("t.")
                && tinkoffToken.length() == 88
                && user.getBroker().equals(SupportedBrokersEnum.MOEX))
        {
            user.setBroker(SupportedBrokersEnum.TINKOFF);
            userUpdateServiceFactory
                    .getUpdateServiceByType(SupportedBrokersEnum.TINKOFF)
                    .fullUserUpdate(user);
        }
    }

    private User findUserByExternalId(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException("User with external id " + externalId + " not found"));
    }

    private User findUserByTelegramNickname(String telegramNickname) {
        return userRepository.findByTelegramNickname(telegramNickname)
                .orElseThrow(() -> new UserNotFoundException("User with Telegram nickname " + telegramNickname + " not found"));
    }
}