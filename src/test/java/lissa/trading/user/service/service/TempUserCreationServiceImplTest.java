package lissa.trading.user.service.service;

import lissa.trading.auth_security_lib.dto.UserInfoDto;
import lissa.trading.user.service.event.TempUserSavedEvent;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.model.TempUserReg;
import org.junit.jupiter.api.Test;
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

class TempUserCreationServiceImplTest extends BaseTest {

    @Test
    void createTempUser_UserAlreadyExists_ThrowsUserCreationException() {
        when(userRepository.findByTelegramNickname(userInfoDto.getTelegramNickname())).thenReturn(Optional.of(user));

        UserCreationException exception = assertThrows(UserCreationException.class, () -> {
            tempUserCreationService.createTempUser(userInfoDto);
        });

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

        doThrow(new DataAccessException("Error saving temp user") {}).when(tempUserRegRepository).save(any(TempUserReg.class));

        Exception exception = assertThrows(DataAccessException.class, () -> tempUserCreationService.createTempUser(userInfoDto));

        assertEquals("Error saving temp user", exception.getMessage());
        verify(tempUserRegMapper, times(2)).toTempUserReg(any(UserInfoDto.class));
        verify(tempUserRegRepository, times(2)).save(any(TempUserReg.class));
        verify(eventPublisher, times(1)).publishEvent(any(TempUserSavedEvent.class));
    }
}