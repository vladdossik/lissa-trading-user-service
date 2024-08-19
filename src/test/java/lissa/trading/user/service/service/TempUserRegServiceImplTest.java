package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.post.UserAuthInfoDto;
import lissa.trading.user.service.event.TempUserSavedEvent;
import lissa.trading.user.service.model.TempUserReg;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TempUserRegServiceImplTest extends BaseTest {

    @Test
    void createTempUser_success() {
        when(tempUserRegMapper.toTempUserReg(any(UserAuthInfoDto.class))).thenReturn(tempUserReg);
        when(tempUserRegRepository.save(any(TempUserReg.class))).thenReturn(tempUserReg);

        tempUserRegService.createTempUser(userAuthInfoDto);

        verify(tempUserRegMapper, times(1)).toTempUserReg(any(UserAuthInfoDto.class));
        verify(tempUserRegRepository, times(1)).save(any(TempUserReg.class));
        verify(eventPublisher, times(1)).publishEvent(any(TempUserSavedEvent.class));
    }

    @Test
    void createTempUser_exceptionThrown() {
        when(tempUserRegMapper.toTempUserReg(any(UserAuthInfoDto.class))).thenReturn(tempUserReg);
        doThrow(new DataAccessException("Error saving temp user") {}).when(tempUserRegRepository).save(any(TempUserReg.class));

        Exception exception = assertThrows(DataAccessException.class, () -> tempUserRegService.createTempUser(userAuthInfoDto));

        assertEquals("Error saving temp user", exception.getMessage());
        verify(tempUserRegMapper, times(1)).toTempUserReg(any(UserAuthInfoDto.class));
        verify(tempUserRegRepository, times(1)).save(any(TempUserReg.class));
        verify(eventPublisher, never()).publishEvent(any(TempUserSavedEvent.class));
    }
}