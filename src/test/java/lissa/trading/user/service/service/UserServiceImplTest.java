package lissa.trading.user.service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User user;
    private UserPostDto userPostDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setExternalId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setTelegramChatId(12345L);
        user.setTelegramNickname("johndoe");
        user.setTinkoffToken("token");
        user.setCurrentBalance(new BigDecimal("100.00"));
        user.setPercentageChangeSinceYesterday(new BigDecimal("0.01"));
        user.setMonetaryChangeSinceYesterday(new BigDecimal("1.00"));
        user.setAccountCount(1);
        user.setIsMarginTradingEnabled(true);
        user.setMarginTradingMetrics("metrics");
        user.setTinkoffInvestmentTariff("tariff");

        userPostDto = new UserPostDto();
        userPostDto.setFirstName("John");
        userPostDto.setLastName("Doe");
        userPostDto.setTelegramChatId(12345L);
        userPostDto.setTelegramNickname("johndoe");
        userPostDto.setTinkoffToken("token");
        userPostDto.setCurrentBalance(new BigDecimal("100.00"));
        userPostDto.setPercentageChangeSinceYesterday(new BigDecimal("0.01"));
        userPostDto.setMonetaryChangeSinceYesterday(new BigDecimal("1.00"));
        userPostDto.setAccountCount(1);
        userPostDto.setIsMarginTradingEnabled(true);
        userPostDto.setMarginTradingMetrics("metrics");
        userPostDto.setTinkoffInvestmentTariff("tariff");

        UserResponseDto userResponseDto = userMapper.toUserResponseDto(user);
    }

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

        UserResponseDto result = userService.updateUser(user.getExternalId(), userPostDto);

        assertNotNull(result);
        assertEquals(user.getFirstName(), result.getFirstName());
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

        Page<UserResponseDto> result = userService.getUsersWithPaginationAndFilters(pageable, "John", "Doe");

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

        Page<UserResponseDto> result = userService.getUsersWithPaginationAndFilters(pageable, "John", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testUpdateUserFromDto_AllFieldsNotNull() {
        UserPostDto userPostDto = new UserPostDto();
        userPostDto.setFirstName("John");
        userPostDto.setLastName("Doe");
        userPostDto.setTelegramChatId(12345L);
        userPostDto.setTelegramNickname("johnny");
        userPostDto.setTinkoffToken("token123");
        userPostDto.setCurrentBalance(BigDecimal.valueOf(1000));
        userPostDto.setPercentageChangeSinceYesterday(BigDecimal.valueOf(5.5));
        userPostDto.setMonetaryChangeSinceYesterday(BigDecimal.valueOf(50));
        userPostDto.setAccountCount(10);
        userPostDto.setIsMarginTradingEnabled(true);
        userPostDto.setMarginTradingMetrics("metrics");
        userPostDto.setTinkoffInvestmentTariff("tariff");

        User user = new User();
        userMapper.updateUserFromDto(userPostDto, user);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(12345L, user.getTelegramChatId());
        assertEquals("johnny", user.getTelegramNickname());
        assertEquals("token123", user.getTinkoffToken());
        assertEquals(BigDecimal.valueOf(1000), user.getCurrentBalance());
        assertEquals(BigDecimal.valueOf(5.5), user.getPercentageChangeSinceYesterday());
        assertEquals(BigDecimal.valueOf(50), user.getMonetaryChangeSinceYesterday());
        assertEquals(10, user.getAccountCount());
        assertTrue(user.getIsMarginTradingEnabled());
        assertEquals("metrics", user.getMarginTradingMetrics());
        assertEquals("tariff", user.getTinkoffInvestmentTariff());
    }

    @Test
    void testUpdateUserFromDto_SomeFieldsNull() {
        UserPostDto userPostDto = getUserPostDto();

        User user = new User();
        userMapper.updateUserFromDto(userPostDto, user);

        assertEquals("John", user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getTelegramChatId());
        assertEquals("johnny", user.getTelegramNickname());
        assertNull(user.getTinkoffToken());
        assertEquals(BigDecimal.valueOf(1000), user.getCurrentBalance());
        assertNull(user.getPercentageChangeSinceYesterday());
        assertEquals(BigDecimal.valueOf(50), user.getMonetaryChangeSinceYesterday());
        assertNull(user.getAccountCount());
        assertTrue(user.getIsMarginTradingEnabled());
        assertNull(user.getMarginTradingMetrics());
        assertEquals("tariff", user.getTinkoffInvestmentTariff());
    }

    private static UserPostDto getUserPostDto() {
        UserPostDto userPostDto = new UserPostDto();
        userPostDto.setFirstName("John");
        userPostDto.setLastName(null);
        userPostDto.setTelegramChatId(null);
        userPostDto.setTelegramNickname("johnny");
        userPostDto.setTinkoffToken(null);
        userPostDto.setCurrentBalance(BigDecimal.valueOf(1000));
        userPostDto.setPercentageChangeSinceYesterday(null);
        userPostDto.setMonetaryChangeSinceYesterday(BigDecimal.valueOf(50));
        userPostDto.setAccountCount(null);
        userPostDto.setIsMarginTradingEnabled(true);
        userPostDto.setMarginTradingMetrics(null);
        userPostDto.setTinkoffInvestmentTariff("tariff");
        return userPostDto;
    }

    @Test
    void testUpdateUserFromDto_NullDto() {
        UserPostDto userPostDto = null;
        User user = new User();

        userMapper.updateUserFromDto(userPostDto, user);

        // Verify that the user fields remain unchanged
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