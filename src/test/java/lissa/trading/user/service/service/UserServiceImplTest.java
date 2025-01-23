package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.notification.OperationEnum;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.exception.UserNotFoundException;
import lissa.trading.user.service.feign.tinkoff.TinkoffAccountClient;
import lissa.trading.user.service.mapper.TempUserRegMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.page.CustomPage;
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
import lissa.trading.user.service.service.update.MoexUpdateServiceImpl;
import lissa.trading.user.service.service.update.TinkoffUpdateServiceImpl;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;
import lissa.trading.user.service.service.update.factory.UpdateServiceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest extends AbstractInitialization {

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected UpdateServiceFactory updateServiceFactory;

    @Mock
    protected UserMapper userMapper;

    @Mock
    protected UserUpdatesPublisher userUpdatesPublisher;

    @Mock
    protected NotificationContext notificationContext;

    @Mock
    protected StatsPublisher statsPublisher;

    @InjectMocks
    protected UserServiceImpl userService;

    @Test
    void updateUser_success() {
        UUID externalId = UUID.randomUUID();
        when(updateServiceFactory.getUpdateServiceByType(SupportedBrokersEnum.TINKOFF))
                .thenReturn(mock(TinkoffUpdateServiceImpl.class));
        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(user));
        when(userMapper.updateUserFromDto(userPatchDto, user)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toUserResponseDto(updatedUser)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateUser(externalId, userPatchDto);

        assertNotNull(result);
        assertEquals("jane", result.getFirstName());
        verify(userRepository).findByExternalId(externalId);
        verify(userMapper).updateUserFromDto(userPatchDto, user);
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
                .thenThrow(new DataAccessException("Database error") {
                });

        DataAccessException thrown = assertThrows(DataAccessException.class, () ->
                userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName));

        assertEquals("Database error", thrown.getMessage());
        verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}