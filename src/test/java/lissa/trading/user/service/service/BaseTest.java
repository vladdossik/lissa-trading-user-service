package lissa.trading.user.service.service;

import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.feign.tinkoff.TinkoffAccountClient;
import lissa.trading.user.service.mapper.TempUserRegMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.BalanceEntity;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.model.entity.MarginTradingMetricsEntity;
import lissa.trading.user.service.model.entity.PostsEntity;
import lissa.trading.user.service.model.entity.UserAccountEntity;
import lissa.trading.user.service.model.entity.UserOperationsEntity;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.BalanceEntityRepository;
import lissa.trading.user.service.repository.entity.FavoriteStocksEntityRepository;
import lissa.trading.user.service.repository.entity.MarginTradingMetricsEntityRepository;
import lissa.trading.user.service.repository.entity.UserAccountEntityRepository;
import lissa.trading.user.service.repository.entity.UserPositionsEntityRepository;
import lissa.trading.user.service.service.creation.UserCreationServiceImpl;
import lissa.trading.user.service.service.creation.temp.TempUserCreationServiceImpl;
import lissa.trading.user.service.service.publisher.NotificationContext;
import lissa.trading.user.service.service.publisher.UserUpdatesPublisher;
import lissa.trading.user.service.service.publisher.stats.StatsPublisher;
import lissa.trading.user.service.service.update.TinkoffUpdateServiceImpl;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;
import lissa.trading.user.service.service.update.factory.UpdateServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public abstract class BaseTest {

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected TempUserRegRepository tempUserRegRepository;

    @Mock
    protected FavoriteStocksEntityRepository favoriteStocksRepository;

    @Mock
    protected UserPositionsEntityRepository userPositionsRepository;

    @Mock
    protected MarginTradingMetricsEntityRepository marginTradingMetricsRepository;

    @Mock
    protected TinkoffAccountClient tinkoffAccountClient;

    @Mock
    protected BalanceEntityRepository balanceRepository;

    @Mock
    protected UserAccountEntityRepository userAccountRepository;

    @Mock
    protected TempUserRegMapper tempUserRegMapper;

    @Mock
    protected ApplicationEventPublisher eventPublisher;

    @Mock
    protected UpdateServiceFactory updateServiceFactory;

    @Mock
    protected TinkoffUpdateServiceImpl tinkoffUpdateService;

    @Mock
    protected UserUpdatesPublisher userUpdatesPublisher;

    @Mock
    protected UserMapper userMapper;

    @Mock
    protected StatsPublisher<User> statsPublisher;

    @Mock
    protected NotificationContext notificationContext;

    @InjectMocks
    protected UserServiceImpl userService;

    @InjectMocks
    protected UserCreationServiceImpl userCreationService;

    @InjectMocks
    protected TempUserCreationServiceImpl tempUserCreationService;

    protected User user;
    protected TempUserReg tempUserReg;
    protected UserPatchDto userPatchDto;
    protected UserInfoDto userInfoDto;
    protected UserResponseDto userResponseDto;
    protected User updatedUser;

    @BeforeEach
    void setUp() {
        user = createUser();
        tempUserReg = createTempUserReg();
        userPatchDto = createUserPatchDto();
        userInfoDto = createUserAuthInfoDto();
        userResponseDto = createUserResponseDto();
        updatedUser = createUpdatedUser();
    }

    private User createUser() {
        User userEntity = new User();
        userEntity.setExternalId(UUID.randomUUID());
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setTelegramChatId(12345L);
        userEntity.setTelegramNickname("johndoe");
        userEntity.setTinkoffToken("t.9zbdzHphp5ZkQzAYFpXmzWS60POh5XCWKUzpluZ2abPPyGtR7" +
                                           "xWZCRNa2yNNlcukVuLq5hjANhepUNoMfKkPBF");
        userEntity.setBroker(SupportedBrokersEnum.TINKOFF);

        BalanceEntity balance = new BalanceEntity();
        balance.setUser(userEntity);
        balance.setCurrency("USD");
        balance.setCurrentBalance(new BigDecimal("100.00"));
        balance.setTotalAmountBalance(new BigDecimal("100.00"));
        userEntity.getBalances().add(balance);

        userEntity.setPercentageChangeSinceYesterday(new BigDecimal("0.01"));
        userEntity.setMonetaryChangeSinceYesterday(new BigDecimal("1.00"));

        userEntity.setAccountCount(1);
        userEntity.setIsMarginTradingEnabled(true);

        MarginTradingMetricsEntity metrics = new MarginTradingMetricsEntity();
        metrics.setUser(userEntity);
        metrics.setCurrency("USD");
        metrics.setLiquidPortfolio(5000.00);
        userEntity.setMarginTradingMetrics(metrics);

        UserAccountEntity account = new UserAccountEntity();
        account.setUser(userEntity);
        userEntity.getUserAccounts().add(account);

        FavoriteStocksEntity favoriteStock = new FavoriteStocksEntity();
        favoriteStock.setUser(userEntity);
        userEntity.getFavoriteStocks().add(favoriteStock);

        UserPositionsEntity position = new UserPositionsEntity();
        position.setUser(userEntity);
        userEntity.getPositions().add(position);

        UserOperationsEntity operation = new UserOperationsEntity();
        operation.setUser(userEntity);
        userEntity.getOperations().add(operation);

        PostsEntity post = new PostsEntity();
        post.setUser(userEntity);
        userEntity.getPosts().add(post);

        return userEntity;
    }

    private TempUserReg createTempUserReg() {
        TempUserReg tempUser = new TempUserReg();
        tempUser.setId(1L);
        tempUser.setExternalId(UUID.randomUUID());
        tempUser.setFirstName("John");
        tempUser.setLastName("Doe");
        tempUser.setTelegramNickname("johndoe");
        tempUser.setTinkoffToken("t.9zbdzHphp5ZkQzAYFpXmzWS60POh5XCWKUzpluZ2abPPyGtR7" +
                                           "xWZCRNa2yNNlcukVuLq5hjANhepUNoMfKkPBF");
        tempUser.setBroker(SupportedBrokersEnum.TINKOFF);
        return tempUser;
    }

    private UserPatchDto createUserPatchDto() {
        UserPatchDto dto = new UserPatchDto();
        dto.setFirstName("Jane");
        dto.setLastName(null);
        dto.setTelegramNickname("");
        dto.setTinkoffToken("t.0zbdzHphp5ZkQzAYFpXmzWS60POh5XCWKUzpluZ2abPPyGtR7" +
                                    "XWZCRNa2yNNlcukVuLq5hjANhepUNoMfKkPBF");
        return dto;
    }

    private User createUpdatedUser() {
        User updatedUser = new User();
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Doe");
        updatedUser.setTelegramChatId(12345L);
        updatedUser.setTelegramNickname("");
        updatedUser.setTinkoffToken("t.0zbdzHphp5ZkQzAYFpXmzWS60POh5XCWKUzpluZ2abPPyGtR7" +
                                    "XWZCRNa2yNNlcukVuLq5hjANhepUNoMfKkPBF");
        return updatedUser;
    }

    private UserInfoDto createUserAuthInfoDto() {
        UserInfoDto dto = new UserInfoDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setTelegramNickname("johndoe");
        dto.setTinkoffToken("t.9zbdzHphp5ZkQzAYFpXmzWS60POh5XCWKUzpluZ2abPPyGtR7" +
                                           "XWZCRNa2yNNlcukVuLq5hjANhepUNoMfKkPBF");
        return dto;
    }

    private UserResponseDto createUserResponseDto() {
        UserResponseDto dto = new UserResponseDto();
        dto.setFirstName("jane");
        dto.setLastName("Doe");
        dto.setTelegramChatId(12345L);
        dto.setTelegramNickname("");
        dto.setTinkoffToken("t.0zbdzHphp5ZkQzAYFpXmzWS60POh5XCWKUzpluZ2abPPyGtR7" +
                                    "XWZCRNa2yNNlcukVuLq5hjANhepUNoMfKkPBF");
        return dto;
    }
}