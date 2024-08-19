package lissa.trading.user.service.service;

import lissa.trading.user.service.exception.UserCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirstInteractionUserRegImplTest extends BaseTest {

    @Spy
    private TempUserRegService spyTempUserRegService;

    @BeforeEach
    void setUpSpy() {
        spyTempUserRegService = spy(tempUserRegService);
        firstInteractionUserReg = new FirstInteractionUserRegImpl(spyTempUserRegService, userRepository);
    }

    @Test
    void createTempUser_UserAlreadyExists_ThrowsUserCreationException() {
        when(userRepository.findByTelegramNickname(userAuthInfoDto.getTelegramNickname())).thenReturn(Optional.of(user));

        UserCreationException exception = assertThrows(UserCreationException.class, () -> {
            firstInteractionUserReg.createTempUser(userAuthInfoDto);
        });

        assertEquals("User with telegram nickname " + userAuthInfoDto.getTelegramNickname() + " already exists!", exception.getMessage());
        verify(spyTempUserRegService, never()).createTempUser(userAuthInfoDto);
    }

    @Test
    void createTempUser_UserDoesNotExist_CreatesTempUser() {
        when(userRepository.findByTelegramNickname(userAuthInfoDto.getTelegramNickname())).thenReturn(Optional.empty());

        firstInteractionUserReg.createTempUser(userAuthInfoDto);

        verify(spyTempUserRegService, times(1)).createTempUser(userAuthInfoDto);
    }
}