package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.notification.OperationEnum;
import lissa.trading.user.service.dto.tinkoff.account.TinkoffTokenDto;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class TinkoffUserCreationServiceImplTest extends BaseTest {

    @Test
    void createUserFromTempUserReg_success() {
        when(userMapper.toUserFromTempUserReg(tempUserReg)).thenReturn(this.user);
        when(updateServiceFactory.getUpdateServiceByType(SupportedBrokersEnum.TINKOFF))
                .thenReturn(tinkoffUpdateService);
        doNothing().when(tinkoffUpdateService).userEntitiesUpdate(any(User.class));
        doNothing().when(userUpdatesPublisher)
                .publishUserUpdateNotification(any(User.class), any(OperationEnum.class));

        when(userRepository.save(any(User.class))).thenReturn(user);

        userCreationService.createUserFromTempUserReg(tempUserReg);

        verify(userMapper).toUserFromTempUserReg(tempUserReg);
        verify(updateServiceFactory).getUpdateServiceByType(SupportedBrokersEnum.TINKOFF);
        verify(tinkoffUpdateService).userEntitiesUpdate(any(User.class));
        verify(userUpdatesPublisher).publishUserUpdateNotification(any(User.class), eq(OperationEnum.REGISTER));
        verify(userRepository).save(user);
        verify(tempUserRegRepository).delete(tempUserReg);
    }

    @Test
    void createUserFromTempUserReg_DataIntegrityViolationExceptionThrown() {
        when(userMapper.toUserFromTempUserReg(tempUserReg)).thenReturn(this.user);
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Unique constraint violated"));

        UserCreationException thrown = assertThrows(UserCreationException.class, () ->
                userCreationService.createUserFromTempUserReg(tempUserReg));

        assertTrue(thrown.getMessage().contains("Table error creating user from TempUserReg"));
        verify(userRepository, times(1)).save(any(User.class));
        verify(tempUserRegRepository, never()).delete(any(TempUserReg.class));
    }

    @Test
    void createUserFromTempUserReg_ExceptionThrown() {
        when(userMapper.toUserFromTempUserReg(tempUserReg)).thenReturn(user);
        when(tinkoffAccountClient.setTinkoffToken(new TinkoffTokenDto(user.getTinkoffToken())))
                .thenThrow(new RuntimeException("Tinkoff API error"));

        UserCreationException thrown = assertThrows(UserCreationException.class, () ->
                userCreationService.createUserFromTempUserReg(tempUserReg));

        assertTrue(thrown.getMessage().contains("Error creating user from TempUserReg"));
        verify(userMapper, times(1)).toUserFromTempUserReg(tempUserReg);
        verify(tinkoffAccountClient, times(1)).setTinkoffToken(any(TinkoffTokenDto.class));
        verify(userRepository, never()).save(any(User.class));
        verify(tempUserRegRepository, never()).delete(any(TempUserReg.class));
    }

    @Test
    void createUserFromTempUserReg_tempUserRegDeletionException() {
        when(userMapper.toUserFromTempUserReg(tempUserReg)).thenReturn(user);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new DataIntegrityViolationException("Cannot delete tempUserReg"))
                .when(tempUserRegRepository).delete(tempUserReg);

        UserCreationException thrown = assertThrows(UserCreationException.class, () ->
                userCreationService.createUserFromTempUserReg(tempUserReg));

        assertTrue(thrown.getMessage().contains("Table error creating user from TempUserReg"));
        verify(userRepository, times(1)).save(any(User.class));
        verify(tempUserRegRepository, times(1)).delete(tempUserReg);
    }
}