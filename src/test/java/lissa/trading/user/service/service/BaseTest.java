package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.post.TempUserRegPostDto;
import lissa.trading.user.service.dto.post.UserPostDto;
import lissa.trading.user.service.mapper.TempUserRegMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

public class BaseTest {
    @Mock
    protected UserRepository userRepository;

    @Mock
    protected TempUserRegRepository tempUserRegRepository;

    protected UserMapper userMapper;
    protected UserService userService;
    protected TempUserRegService tempUserRegService;
    protected UserCreationService userCreationService;
    protected TempUserRegServiceImpl tempUserRegServiceImpl;

    protected User user;
    protected TempUserReg tempUserReg;
    protected UserPostDto userPostDto;
    protected TempUserRegPostDto tempUserRegPostDto;
    protected UserPatchDto userPatchDto;

    @Mock
    protected TempUserRegMapper tempUserRegMapper;

    @Mock
    protected ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
        tempUserRegService = new TempUserRegServiceImpl(tempUserRegRepository, tempUserRegMapper, eventPublisher);
        userCreationService = new UserCreationServiceImpl(userRepository, tempUserRegRepository, userMapper);

        tempUserRegServiceImpl = new TempUserRegServiceImpl(tempUserRegRepository, tempUserRegMapper, eventPublisher);

        user = new User();
        user.setId(1L);
        user.setExternalId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setTelegramChatId(12345L);
        user.setTelegramNickname("johndoe");
        user.setTinkoffToken("token");
        user.setCurrentBalance(new BigDecimal("100.00"));
        user.setPercentageChangeSinceYesterday(new BigDecimal("0.01"));
        user.setMonetaryChangeSinceYesterday(new BigDecimal("1.00"));
        user.setAccountCount(1);
        user.setIsMarginTradingEnabled(true);
        user.setMarginTradingMetrics("metrics");
        user.setTinkoffInvestmentTariff("tariff");

        tempUserReg = new TempUserReg();
        tempUserReg.setId(1L);
        tempUserReg.setExternalId(UUID.randomUUID());
        tempUserReg.setFirstName("John");
        tempUserReg.setLastName("Doe");
        tempUserReg.setTelegramNickname("johndoe");

        userPostDto = new UserPostDto();
        userPostDto.setFirstName("John");
        userPostDto.setLastName("Doe");
        userPostDto.setTelegramNickname("johndoe");
        userPostDto.setTinkoffToken("token");

        tempUserRegPostDto = new TempUserRegPostDto();
        tempUserRegPostDto.setFirstName("John");
        tempUserRegPostDto.setLastName("Doe");
        tempUserRegPostDto.setTelegramNickname("johndoe");
        tempUserRegPostDto.setTinkoffToken("token");

        userPatchDto = new UserPatchDto();
        userPatchDto.setFirstName("Jane");
        userPatchDto.setLastName(null);
        userPatchDto.setTelegramNickname("");
        userPatchDto.setTinkoffToken("newToken");
    }
}