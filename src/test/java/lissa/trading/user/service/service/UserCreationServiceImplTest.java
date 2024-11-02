package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.tinkoff.UserAccount;
import lissa.trading.user.service.dto.tinkoff.account.AccountInfoDto;
import lissa.trading.user.service.dto.tinkoff.account.BalanceDto;
import lissa.trading.user.service.dto.tinkoff.account.FavouriteStocksDto;
import lissa.trading.user.service.dto.tinkoff.account.MarginAttributesDto;
import lissa.trading.user.service.dto.tinkoff.account.SecurityPosition;
import lissa.trading.user.service.dto.tinkoff.account.SecurityPositionsDto;
import lissa.trading.user.service.dto.tinkoff.account.TinkoffTokenDto;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.BalanceEntity;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.model.entity.MarginTradingMetricsEntity;
import lissa.trading.user.service.model.entity.UserAccountEntity;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserCreationServiceImplTest extends BaseTest {

    @Test
    void createUserFromTempUserReg_success() {
        User user = new User();
        when(userMapper.toUserFromTempUserReg(tempUserReg)).thenReturn(user);

        AccountInfoDto accountsInfoDto = new AccountInfoDto();
        UserAccount userAccount = new UserAccount();
        userAccount.setAccountId("accountId123");
        userAccount.setTariff("Standard");
        userAccount.setPremStatus(true);
        accountsInfoDto.setUserAccounts(Collections.singletonList(userAccount));
        when(tinkoffAccountClient.getAccountsInfo()).thenReturn(accountsInfoDto);

        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setCurrency("USD");
        balanceDto.setCurrentBalance(new BigDecimal("1000"));
        balanceDto.setTotalAmountBalance(new BigDecimal("1000"));
        when(tinkoffAccountClient.getPortfolio("accountId123")).thenReturn(balanceDto);

        MarginAttributesDto marginAttributesDto = new MarginAttributesDto();
        marginAttributesDto.setCurrency("USD");
        marginAttributesDto.setLiquidPortfolio(5000.00);
        when(tinkoffAccountClient.getMarginAttributes("accountId123")).thenReturn(marginAttributesDto);

        FavouriteStocksDto favouriteStocksDto = new FavouriteStocksDto();
        favouriteStocksDto.setFavouriteStocks(Arrays.asList("AAPL", "GOOG"));
        when(tinkoffAccountClient.getFavouriteStocks()).thenReturn(favouriteStocksDto);

        SecurityPositionsDto securityPositionsDto = new SecurityPositionsDto();
        SecurityPosition securityPosition = new SecurityPosition();
        securityPosition.setFigi("figi123");
        securityPosition.setBalance(10);
        securityPosition.setBlocked(0);
        securityPositionsDto.setPositions(Collections.singletonList(securityPosition));
        when(tinkoffAccountClient.getPositionsById("accountId123")).thenReturn(securityPositionsDto);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(balanceRepository.save(any(BalanceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(marginTradingMetricsRepository.save(any(MarginTradingMetricsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(favoriteStocksRepository.save(any(FavoriteStocksEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userPositionsRepository.save(any(UserPositionsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userAccountRepository.save(any(UserAccountEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userCreationService.createUserFromTempUserReg(tempUserReg);

        verify(userMapper).toUserFromTempUserReg(tempUserReg);
        verify(tinkoffAccountClient).setTinkoffToken(any(TinkoffTokenDto.class));
        verify(tinkoffAccountClient, times(2)).getAccountsInfo(); // getTinkoffAccountId and updateUserAccounts
        verify(tinkoffAccountClient).getPortfolio("accountId123");
        verify(balanceRepository).save(any(BalanceEntity.class));
        verify(tinkoffAccountClient).getMarginAttributes("accountId123");
        verify(marginTradingMetricsRepository).save(any(MarginTradingMetricsEntity.class));
        verify(tinkoffAccountClient).getFavouriteStocks();
        verify(favoriteStocksRepository, times(2)).save(any(FavoriteStocksEntity.class));
        verify(tinkoffAccountClient).getPositionsById("accountId123");
        verify(userPositionsRepository).save(any(UserPositionsEntity.class));
        verify(userRepository, times(2)).save(any(User.class)); // Once in updateUserFromTinkoffData, once at the end
        verify(tempUserRegRepository).delete(tempUserReg);
    }

    @Test
    void createUserFromTempUserReg_DataIntegrityViolationExceptionThrown() {
        User user = new User();
        when(userMapper.toUserFromTempUserReg(tempUserReg)).thenReturn(user);

        AccountInfoDto accountsInfoDto = new AccountInfoDto();
        UserAccount userAccount = new UserAccount();
        userAccount.setAccountId("accountId123");
        accountsInfoDto.setUserAccounts(Collections.singletonList(userAccount));
        when(tinkoffAccountClient.getAccountsInfo()).thenReturn(accountsInfoDto);

        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setCurrency("USD");
        balanceDto.setCurrentBalance(new BigDecimal("1000"));
        balanceDto.setTotalAmountBalance(new BigDecimal("1000"));
        when(tinkoffAccountClient.getPortfolio("accountId123")).thenReturn(balanceDto);

        MarginAttributesDto marginAttributesDto = new MarginAttributesDto();
        marginAttributesDto.setCurrency("USD");
        marginAttributesDto.setLiquidPortfolio(5000.00);
        when(tinkoffAccountClient.getMarginAttributes("accountId123")).thenReturn(marginAttributesDto);

        FavouriteStocksDto favouriteStocksDto = new FavouriteStocksDto();
        favouriteStocksDto.setFavouriteStocks(Collections.emptyList());
        when(tinkoffAccountClient.getFavouriteStocks()).thenReturn(favouriteStocksDto);

        SecurityPositionsDto securityPositionsDto = new SecurityPositionsDto();
        securityPositionsDto.setPositions(Collections.emptyList());
        when(tinkoffAccountClient.getPositionsById("accountId123")).thenReturn(securityPositionsDto);

        when(balanceRepository.save(any(BalanceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(marginTradingMetricsRepository.save(any(MarginTradingMetricsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userAccountRepository.save(any(UserAccountEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(userRepository.save(any(User.class)))
                .thenReturn(user)
                .thenThrow(new DataIntegrityViolationException("Unique constraint violated")); // Second call throws exception

        UserCreationException thrown = assertThrows(UserCreationException.class, () ->
                userCreationService.createUserFromTempUserReg(tempUserReg));

        assertTrue(thrown.getMessage().contains("Table error creating user from TempUserReg"));
        verify(userRepository, times(2)).save(any(User.class));
        verify(tempUserRegRepository, never()).delete(any(TempUserReg.class));
    }



    @Test
    void createUserFromTempUserReg_ExceptionThrown() {
        User user = new User();
        when(userMapper.toUserFromTempUserReg(tempUserReg)).thenReturn(user);
        when(tinkoffAccountClient.getAccountsInfo()).thenThrow(new RuntimeException("Tinkoff API error"));

        UserCreationException thrown = assertThrows(UserCreationException.class, () ->
                userCreationService.createUserFromTempUserReg(tempUserReg));

        assertTrue(thrown.getMessage().contains("Error creating user from TempUserReg"));
        verify(userMapper, times(1)).toUserFromTempUserReg(tempUserReg);
        verify(tinkoffAccountClient, times(1)).setTinkoffToken(any(TinkoffTokenDto.class));
        verify(tinkoffAccountClient, times(1)).getAccountsInfo();
        verify(userRepository, never()).save(any(User.class));
        verify(tempUserRegRepository, never()).delete(any(TempUserReg.class));
    }

    @Test
    void createUserFromTempUserReg_tempUserRegDeletionException() {
        User user = new User();
        when(userMapper.toUserFromTempUserReg(tempUserReg)).thenReturn(user);

        AccountInfoDto accountsInfoDto = new AccountInfoDto();
        UserAccount userAccount = new UserAccount();
        userAccount.setAccountId("accountId123");
        userAccount.setTariff("Standard");
        userAccount.setPremStatus(true);
        accountsInfoDto.setUserAccounts(Collections.singletonList(userAccount));
        when(tinkoffAccountClient.getAccountsInfo()).thenReturn(accountsInfoDto);

        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setCurrency("USD");
        balanceDto.setCurrentBalance(new BigDecimal("1000"));
        balanceDto.setTotalAmountBalance(new BigDecimal("1000"));
        when(tinkoffAccountClient.getPortfolio("accountId123")).thenReturn(balanceDto);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(balanceRepository.save(any(BalanceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new DataIntegrityViolationException("Cannot delete tempUserReg"))
                .when(tempUserRegRepository).delete(tempUserReg);

        UserCreationException thrown = assertThrows(UserCreationException.class, () ->
                userCreationService.createUserFromTempUserReg(tempUserReg));

        assertTrue(thrown.getMessage().contains("Table error creating user from TempUserReg"));
        verify(userRepository, times(2)).save(any(User.class)); // Once in updateUserFromTinkoffData, once at the end
        verify(tempUserRegRepository, times(1)).delete(tempUserReg);
    }
}