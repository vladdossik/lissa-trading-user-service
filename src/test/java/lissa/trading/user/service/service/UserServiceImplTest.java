package lissa.trading.user.service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lissa.trading.user.service.dto.UserPatchDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.page.CustomPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest extends InitializationClass {

    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.createUser(userPostDto);

        assertNotNull(result);
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findByExternalId(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.updateUser(user.getExternalId(), userPatchDto);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName()); // Изменение проверки на существующее значение
        verify(userRepository, times(1)).findByExternalId(any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testBlockUserByTelegramNickname() {
        when(userRepository.findByTelegramNickname(anyString())).thenReturn(Optional.of(user));

        userService.blockUserByTelegramNickname(user.getTelegramNickname());

        assertFalse(user.getIsMarginTradingEnabled());
        verify(userRepository, times(1)).findByTelegramNickname(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUserByExternalId() {
        when(userRepository.findByExternalId(any(UUID.class))).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUserByExternalId(user.getExternalId());

        verify(userRepository, times(1)).findByExternalId(any(UUID.class));
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void testGetUserByExternalId() {
        when(userRepository.findByExternalId(any(UUID.class))).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserByExternalId(user.getExternalId());

        assertNotNull(result);
        assertEquals(user.getFirstName(), result.getFirstName());
        verify(userRepository, times(1)).findByExternalId(any(UUID.class));
    }

    @Test
    void testGetUsersWithPaginationAndFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(user);
        Page<User> page = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        CustomPage<UserResponseDto> result = userService.getUsersWithPaginationAndFilters(pageable, "John", "Doe");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetUsersWithPaginationAndFilters_OnlyFirstName() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(user);
        Page<User> page = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        CustomPage<UserResponseDto> result = userService.getUsersWithPaginationAndFilters(pageable, "John", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testUpdateUserWithPatchDto() {
        when(userRepository.findByExternalId(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.updateUser(user.getExternalId(), userPatchDto);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(12345L, result.getTelegramChatId());
        assertNull(result.getTelegramNickname());
        assertEquals("newToken", result.getTinkoffToken());
        assertEquals(new BigDecimal("100.00"), result.getCurrentBalance());
        assertEquals(new BigDecimal("0.01"), result.getPercentageChangeSinceYesterday());
        assertEquals(new BigDecimal("1.00"), result.getMonetaryChangeSinceYesterday());
        assertEquals(1, result.getAccountCount().intValue());
        assertTrue(result.getIsMarginTradingEnabled());
        assertEquals("metrics", result.getMarginTradingMetrics());
        assertEquals("tariff", result.getTinkoffInvestmentTariff());
        verify(userRepository, times(1)).findByExternalId(any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserWithPatchDto_PartialUpdate() {
        UserPatchDto partialUpdateDto = new UserPatchDto();
        partialUpdateDto.setFirstName("Jane");
        partialUpdateDto.setLastName("");
        partialUpdateDto.setTelegramNickname(null);
        partialUpdateDto.setTinkoffToken("token");

        when(userRepository.findByExternalId(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.updateUser(user.getExternalId(), partialUpdateDto);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertNull(result.getLastName());
        assertEquals(user.getTelegramChatId(), result.getTelegramChatId());
        assertEquals(user.getTelegramNickname(), result.getTelegramNickname());
        assertEquals(user.getTinkoffToken(), result.getTinkoffToken());
        assertEquals(user.getCurrentBalance(), result.getCurrentBalance());
        assertEquals(user.getPercentageChangeSinceYesterday(), result.getPercentageChangeSinceYesterday());
        assertEquals(user.getMonetaryChangeSinceYesterday(), result.getMonetaryChangeSinceYesterday());
        assertEquals(user.getAccountCount(), result.getAccountCount());
        assertEquals(user.getIsMarginTradingEnabled(), result.getIsMarginTradingEnabled());
        assertEquals(user.getMarginTradingMetrics(), result.getMarginTradingMetrics());
        assertEquals(user.getTinkoffInvestmentTariff(), result.getTinkoffInvestmentTariff());
        verify(userRepository, times(1)).findByExternalId(any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserFromDto_SomeFieldsNull() {

        UserPatchDto nullUserPatchDto = new UserPatchDto();
        nullUserPatchDto.setFirstName("John");
        nullUserPatchDto.setLastName(null);
        nullUserPatchDto.setTelegramNickname("johnny");
        nullUserPatchDto.setTinkoffToken("");

        User user = new User();
        userMapper.updateUserFromDto(nullUserPatchDto, user);

        assertEquals("John", user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getTelegramChatId());
        assertEquals("johnny", user.getTelegramNickname());
        assertNull(user.getTinkoffToken());
        assertNull(user.getCurrentBalance());
        assertNull(user.getPercentageChangeSinceYesterday());
        assertNull(user.getMonetaryChangeSinceYesterday());
        assertNull(user.getAccountCount());
        assertNull(user.getIsMarginTradingEnabled());
        assertNull(user.getMarginTradingMetrics());
        assertNull(user.getTinkoffInvestmentTariff());
    }

    @Test
    void testUpdateUserFromDto_NullDto() {
        User user = new User();

        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getTelegramChatId());
        assertNull(user.getTelegramNickname());
        assertNull(user.getTinkoffToken());
        assertNull(user.getCurrentBalance());
        assertNull(user.getPercentageChangeSinceYesterday());
        assertNull(user.getMonetaryChangeSinceYesterday());
        assertNull(user.getAccountCount());
        assertNull(user.getIsMarginTradingEnabled());
        assertNull(user.getMarginTradingMetrics());
        assertNull(user.getTinkoffInvestmentTariff());
    }
}