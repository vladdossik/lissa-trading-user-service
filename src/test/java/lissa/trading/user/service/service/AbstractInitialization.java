package lissa.trading.user.service.service;

import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.BalanceEntity;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.model.entity.MarginTradingMetricsEntity;
import lissa.trading.user.service.model.entity.PostsEntity;
import lissa.trading.user.service.model.entity.UserAccountEntity;
import lissa.trading.user.service.model.entity.UserOperationsEntity;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@Slf4j
public abstract class AbstractInitialization {

    protected static User user;
    protected static TempUserReg tempUserReg;
    protected static UserPatchDto userPatchDto;
    protected static UserInfoDto userInfoDto;
    protected static UserResponseDto userResponseDto;
    protected static User updatedUser;

    @BeforeAll
    public static void init() {
        log.info("INIIIIIIIIIIIIIIT");
        user = createUser();
        tempUserReg = createTempUserReg();
        userPatchDto = createUserPatchDto();
        userInfoDto = createUserAuthInfoDto();
        userResponseDto = createUserResponseDto();
        updatedUser = createUpdatedUser();
    }

    private static User createUser() {
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

    private static TempUserReg createTempUserReg() {
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

    private static UserPatchDto createUserPatchDto() {
        UserPatchDto dto = new UserPatchDto();
        dto.setFirstName("Jane");
        dto.setLastName(null);
        dto.setTelegramNickname("");
        dto.setTinkoffToken("t.0zbdzHphp5ZkQzAYFpXmzWS60POh5XCWKUzpluZ2abPPyGtR7" +
                                    "XWZCRNa2yNNlcukVuLq5hjANhepUNoMfKkPBF");
        return dto;
    }

    private static User createUpdatedUser() {
        User updatedUser = new User();
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Doe");
        updatedUser.setTelegramChatId(12345L);
        updatedUser.setTelegramNickname("");
        updatedUser.setTinkoffToken("t.0zbdzHphp5ZkQzAYFpXmzWS60POh5XCWKUzpluZ2abPPyGtR7" +
                                            "XWZCRNa2yNNlcukVuLq5hjANhepUNoMfKkPBF");
        return updatedUser;
    }

    private static UserInfoDto createUserAuthInfoDto() {
        UserInfoDto dto = new UserInfoDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setTelegramNickname("johndoe");
        dto.setTinkoffToken("t.9zbdzHphp5ZkQzAYFpXmzWS60POh5XCWKUzpluZ2abPPyGtR7" +
                                    "XWZCRNa2yNNlcukVuLq5hjANhepUNoMfKkPBF");
        return dto;
    }

    private static UserResponseDto createUserResponseDto() {
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
