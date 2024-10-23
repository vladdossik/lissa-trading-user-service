package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.exception.UserNotFoundException;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.page.CustomPage;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceImplTest extends BaseTest {

    @Test
    void updateUser_success() {
        UUID externalId = UUID.randomUUID();
        UserPatchDto userUpdates = new UserPatchDto();
        userUpdates.setFirstName("UpdatedFirstName");
        User existingUser = new User();
        existingUser.setExternalId(externalId);
        User updatedUser = new User();
        updatedUser.setExternalId(externalId);
        updatedUser.setFirstName("UpdatedFirstName");
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setFirstName("UpdatedFirstName");

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(existingUser));
        when(userMapper.updateUserFromDto(userUpdates, existingUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toUserResponseDto(updatedUser)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateUser(externalId, userUpdates);

        assertNotNull(result);
        assertEquals("UpdatedFirstName", result.getFirstName());
        verify(userRepository).findByExternalId(externalId);
        verify(userMapper).updateUserFromDto(userUpdates, existingUser);
        verify(userRepository).save(updatedUser);
        verify(userMapper).toUserResponseDto(updatedUser);
    }

    @Test
    void updateUser_userNotFound() {
        UUID externalId = UUID.randomUUID();
        UserPatchDto userUpdates = new UserPatchDto();
        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(externalId, userUpdates));

        assertEquals("User with external id " + externalId + " not found", thrown.getMessage());
        verify(userRepository, times(1)).findByExternalId(externalId);
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void blockUserByTelegramNickname_success() {
        String telegramNickname = "testNickname";
        User user = new User();
        user.setIsMarginTradingEnabled(true);

        when(userRepository.findByTelegramNickname(telegramNickname)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.blockUserByTelegramNickname(telegramNickname);

        assertFalse(user.getIsMarginTradingEnabled());
        verify(userRepository, times(1)).findByTelegramNickname(telegramNickname);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void blockUserByTelegramNickname_userNotFound() {
        String telegramNickname = "nonExistentNickname";
        when(userRepository.findByTelegramNickname(telegramNickname)).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                userService.blockUserByTelegramNickname(telegramNickname));

        assertEquals("User with Telegram nickname " + telegramNickname + " not found", thrown.getMessage());
        verify(userRepository, times(1)).findByTelegramNickname(telegramNickname);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUserByExternalId_success() {
        UUID externalId = UUID.randomUUID();
        User user = new User();
        user.setExternalId(externalId);

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUserByExternalId(externalId);

        verify(userRepository, times(1)).findByExternalId(externalId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUserByExternalId_userNotFound() {
        UUID externalId = UUID.randomUUID();
        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                userService.deleteUserByExternalId(externalId));

        assertEquals("User with external id " + externalId + " not found", thrown.getMessage());
        verify(userRepository, times(1)).findByExternalId(externalId);
        verify(userRepository, never()).delete((User) any());
    }

    @Test
    void getUserByExternalId_success() {
        UUID externalId = UUID.randomUUID();
        User user = new User();
        user.setExternalId(externalId);
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setExternalId(externalId);

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUserByExternalId(externalId);

        assertNotNull(result);
        assertEquals(externalId, result.getExternalId());
        verify(userRepository, times(1)).findByExternalId(externalId);
        verify(userMapper, times(1)).toUserResponseDto(user);
    }

    @Test
    void getUserByExternalId_userNotFound() {
        UUID externalId = UUID.randomUUID();
        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                userService.getUserByExternalId(externalId));

        assertEquals("User with external id " + externalId + " not found", thrown.getMessage());
        verify(userRepository, times(1)).findByExternalId(externalId);
        verify(userMapper, never()).toUserResponseDto(any());
    }

    @Test
    void getUsersWithPaginationAndFilters_success() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "John";
        String lastName = "Doe";
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setFirstName(firstName);
        userResponseDto.setLastName(lastName);

        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        CustomPage<UserResponseDto> result = userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(firstName, result.getContent().get(0).getFirstName());
        verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(userMapper, times(1)).toUserResponseDto(user);
    }

    @Test
    void getUsersWithPaginationAndFilters_noResults() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "NonExistentFirstName";
        String lastName = "NonExistentLastName";
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        CustomPage<UserResponseDto> result = userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(userMapper, never()).toUserResponseDto(any());
    }

    @Test
    void getUsersWithPaginationAndFilters_exception() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "John";
        String lastName = "Doe";

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenThrow(new DataAccessException("Database error") {});

        DataAccessException thrown = assertThrows(DataAccessException.class, () ->
                userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName));

        assertEquals("Database error", thrown.getMessage());
        verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}