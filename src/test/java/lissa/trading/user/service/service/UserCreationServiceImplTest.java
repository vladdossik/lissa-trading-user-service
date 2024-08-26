package lissa.trading.user.service.service;

import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserCreationServiceImplTest extends BaseTest {

    @Test
    void createUserFromTempUserReg_success() {
        when(userRepository.save(any(User.class))).thenReturn(null);
        doNothing().when(tempUserRegRepository).delete(any(TempUserReg.class));

        userCreationService.createUserFromTempUserReg(tempUserReg);

        verify(userRepository, times(1)).save(any(User.class));
        verify(tempUserRegRepository, times(1)).delete(any(TempUserReg.class));
    }

    @Test
    void createUserFromTempUserReg_exceptionThrown() {
        doThrow(new DataAccessException("Error saving user") {}).when(userRepository).save(any(User.class));

        UserCreationException thrown = assertThrows(UserCreationException.class, () -> userCreationService.createUserFromTempUserReg(tempUserReg));

        assertEquals("Error creating user from TempUserReg", thrown.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
        verify(tempUserRegRepository, never()).delete(any(TempUserReg.class));
    }

    @Test
    void createUserFromTempUserReg_tempUserRegDeletionException() {
        when(userRepository.save(any(User.class))).thenReturn(null);
        doThrow(new DataAccessException("Error deleting TempUserReg") {}).when(tempUserRegRepository).delete(any(TempUserReg.class));

        UserCreationException thrown = assertThrows(UserCreationException.class, () -> userCreationService.createUserFromTempUserReg(tempUserReg));

        assertEquals("Error creating user from TempUserReg", thrown.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
        verify(tempUserRegRepository, times(1)).delete(any(TempUserReg.class));
    }
}