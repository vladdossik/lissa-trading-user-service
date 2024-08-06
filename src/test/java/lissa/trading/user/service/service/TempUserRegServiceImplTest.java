package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.post.TempUserRegPostDto;
import lissa.trading.user.service.dto.response.TempUserRegResponseDto;
import lissa.trading.user.service.handler.TempUserCreatedEvent;
import lissa.trading.user.service.model.TempUserReg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TempUserRegServiceImplTest extends BaseTest {

    @Test
    void createTempUser_success() {
        when(tempUserRegMapper.toTempUserReg(any(TempUserRegPostDto.class))).thenReturn(tempUserReg);
        when(tempUserRegRepository.save(any(TempUserReg.class))).thenReturn(tempUserReg);
        when(tempUserRegMapper.toTempUserRegResponseDto(any(TempUserReg.class))).thenReturn(new TempUserRegResponseDto());

        TempUserRegResponseDto response = tempUserRegServiceImpl.createTempUser(tempUserRegPostDto);

        assertNotNull(response);
        verify(tempUserRegMapper, times(1)).toTempUserReg(any(TempUserRegPostDto.class));
        verify(tempUserRegRepository, times(1)).save(any(TempUserReg.class));
        verify(tempUserRegMapper, times(1)).toTempUserRegResponseDto(any(TempUserReg.class));
        verify(eventPublisher, times(1)).publishEvent(any(TempUserCreatedEvent.class));
    }

    @Test
    void createTempUser_exceptionThrown() {
        when(tempUserRegMapper.toTempUserReg(any(TempUserRegPostDto.class))).thenReturn(tempUserReg);
        doThrow(new DataAccessException("Error saving temp user") {}).when(tempUserRegRepository).save(any(TempUserReg.class));

        Exception exception = assertThrows(DataAccessException.class, () -> tempUserRegServiceImpl.createTempUser(tempUserRegPostDto));

        assertEquals("Error saving temp user", exception.getMessage());
        verify(tempUserRegMapper, times(1)).toTempUserReg(any(TempUserRegPostDto.class));
        verify(tempUserRegRepository, times(1)).save(any(TempUserReg.class));
        verify(tempUserRegMapper, never()).toTempUserRegResponseDto(any(TempUserReg.class));
        verify(eventPublisher, never()).publishEvent(any(TempUserCreatedEvent.class));
    }
}