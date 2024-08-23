package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.post.UserInfoDto;
import lissa.trading.user.service.mapper.TempUserRegMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.service.creation.TempUserCreationService;
import lissa.trading.user.service.service.creation.TempUserCreationServiceImpl;
import lissa.trading.user.service.service.creation.UserCreationService;
import lissa.trading.user.service.service.creation.UserCreationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
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
    protected TempUserRegMapper tempUserRegMapper;

    @Mock
    protected ApplicationEventPublisher eventPublisher;

    protected UserMapper userMapper;
    protected UserService userService;
    protected UserCreationService userCreationService;
    protected TempUserCreationService tempUserCreationService;

    protected User user;
    protected TempUserReg tempUserReg;
    protected UserPatchDto userPatchDto;
    protected UserInfoDto userInfoDto;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
        userCreationService = new UserCreationServiceImpl(userRepository, tempUserRegRepository, userMapper);
        tempUserCreationService = new TempUserCreationServiceImpl(userRepository, tempUserRegRepository, tempUserRegMapper, eventPublisher);

        user = createUser();
        tempUserReg = createTempUserReg();
        userPatchDto = createUserPatchDto();
        userInfoDto = createUserAuthInfoDto();
    }

    private User createUser() {
        User userEntity = new User();
        userEntity.setId(1L);
        userEntity.setExternalId(UUID.randomUUID());
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setTelegramChatId(12345L);
        userEntity.setTelegramNickname("johndoe");
        userEntity.setTinkoffToken("token");
        userEntity.setCurrentBalance(new BigDecimal("100.00"));
        userEntity.setPercentageChangeSinceYesterday(new BigDecimal("0.01"));
        userEntity.setMonetaryChangeSinceYesterday(new BigDecimal("1.00"));
        userEntity.setAccountCount(1);
        userEntity.setIsMarginTradingEnabled(true);
        userEntity.setMarginTradingMetrics("metrics");
        userEntity.setTinkoffInvestmentTariff("tariff");
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