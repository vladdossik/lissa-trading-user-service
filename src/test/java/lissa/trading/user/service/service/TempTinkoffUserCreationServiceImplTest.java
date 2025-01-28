package lissa.trading.user.service.service;

import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.user.service.event.TempUserSavedEvent;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.feign.tinkoff.TinkoffAccountClient;
import lissa.trading.user.service.mapper.TempUserRegMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.BalanceEntityRepository;
import lissa.trading.user.service.repository.entity.FavoriteStocksEntityRepository;
import lissa.trading.user.service.repository.entity.MarginTradingMetricsEntityRepository;
import lissa.trading.user.service.repository.entity.UserAccountEntityRepository;
import lissa.trading.user.service.repository.entity.UserPositionsEntityRepository;
import lissa.trading.user.service.service.consumer.NotificationContext;
import lissa.trading.user.service.service.creation.UserCreationServiceImpl;
import lissa.trading.user.service.service.creation.temp.TempUserCreationServiceImpl;
import lissa.trading.user.service.service.publisher.UserUpdatesPublisher;
import lissa.trading.user.service.service.publisher.stats.StatsPublisher;
import lissa.trading.user.service.service.update.TinkoffUpdateServiceImpl;
import lissa.trading.user.service.service.update.factory.UpdateServiceFactory;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TempTinkoffUserCreationServiceImplTest extends AbstractInitialization {

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected TempUserRegRepository tempUserRegRepository;

    @Mock
    protected TempUserRegMapper tempUserRegMapper;

    @Mock
    protected ApplicationEventPublisher eventPublisher;

    @InjectMocks
    protected TempUserCreationServiceImpl tempUserCreationService;

    @Test
    void createTempUser_UserAlreadyExists_ThrowsUserCreationException() {
        when(userRepository.findByTelegramNickname(userInfoDto.getTelegramNickname())).thenReturn(Optional.of(user));

        UserCreationException exception = assertThrows(UserCreationException.class, () -> tempUserCreationService.createTempUser(userInfoDto));

        assertEquals("User with telegram nickname " + userInfoDto.getTelegramNickname() + " already exists!", exception.getMessage());
        verify(tempUserRegRepository, never()).save(any(TempUserReg.class));
        verify(eventPublisher, never()).publishEvent(any(TempUserSavedEvent.class));
    }

    @Test
    void createTempUser_UserDoesNotExist_CreatesTempUserOrThrowsDataAccessException() {
        when(userRepository.findByTelegramNickname(userInfoDto.getTelegramNickname())).thenReturn(Optional.empty());
        when(tempUserRegMapper.toTempUserReg(any(UserInfoDto.class))).thenReturn(tempUserReg);

        when(tempUserRegRepository.save(any(TempUserReg.class))).thenReturn(tempUserReg);

        tempUserCreationService.createTempUser(userInfoDto);

        verify(tempUserRegMapper, times(1)).toTempUserReg(any(UserInfoDto.class));
        verify(tempUserRegRepository, times(1)).save(any(TempUserReg.class));
        verify(eventPublisher, times(1)).publishEvent(any(TempUserSavedEvent.class));

        doThrow(new DataAccessException("Error saving temp user") {
        }).when(tempUserRegRepository).save(any(TempUserReg.class));

        Exception exception = assertThrows(DataAccessException.class, () -> tempUserCreationService.createTempUser(userInfoDto));

        assertEquals("Error saving temp user", exception.getMessage());
        verify(tempUserRegMapper, times(2)).toTempUserReg(any(UserInfoDto.class));
        verify(tempUserRegRepository, times(2)).save(any(TempUserReg.class));
        verify(eventPublisher, times(1)).publishEvent(any(TempUserSavedEvent.class));
    }
}