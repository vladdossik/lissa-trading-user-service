package lissa.trading.user.service.service;

import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.feign.TinkoffAccountClient;
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
import lissa.trading.user.service.service.creation.TempUserCreationServiceImpl;
import lissa.trading.user.service.service.creation.UserCreationServiceImpl;
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
    protected UserMapper userMapper;

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

    @BeforeEach
    void setUp() {

        user = createUser();
        tempUserReg = createTempUserReg();
        userPatchDto = createUserPatchDto();
        userInfoDto = createUserAuthInfoDto();
    }

    private User createUser() {
        User userEntity = new User();
        userEntity.setExternalId(UUID.randomUUID());
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setTelegramChatId(12345L);
        userEntity.setTelegramNickname("johndoe");
        userEntity.setTinkoffToken("token");

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
        return tempUser;
    }

    private UserPatchDto createUserPatchDto() {
        UserPatchDto dto = new UserPatchDto();
        dto.setFirstName("Jane");
        dto.setLastName(null);
        dto.setTelegramNickname("");
        dto.setTinkoffToken("newToken");
        return dto;
    }

    private UserInfoDto createUserAuthInfoDto() {
        UserInfoDto dto = new UserInfoDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setTelegramNickname("johndoe");
        dto.setTinkoffToken("token");
        return dto;
    }
}
