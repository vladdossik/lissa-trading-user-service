package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.UserPatchDto;
import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.UUID;

public class ServiceInitializationClass {

    @Mock
    protected UserRepository userRepository;

    protected UserServiceImpl userService;
    protected UserMapper userMapper;

    protected User user;
    protected UserPostDto userPostDto;
    protected UserPatchDto userPatchDto;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);

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

        userPostDto = new UserPostDto();
        userPostDto.setFirstName("John");
        userPostDto.setLastName("Doe");
        userPostDto.setTelegramNickname("johndoe");
        userPostDto.setTinkoffToken("token");

        userPatchDto = new UserPatchDto();
        userPatchDto.setFirstName("Jane");
        userPatchDto.setLastName(null);
        userPatchDto.setTelegramNickname("");
        userPatchDto.setTinkoffToken("newToken");
    }
}
