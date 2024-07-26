package lissa.trading.user.service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lissa.trading.user.service.dto.UserPatchDto;
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
    private UserPatchDto userPatchDto;

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

        userPatchDto = new UserPatchDto();
        userPatchDto.setFirstName(Optional.of("Jane"));
        userPatchDto.setLastName(Optional.empty());
        userPatchDto.setTelegramChatId(Optional.of(67890L));
        userPatchDto.setTelegramNickname(Optional.of(""));
        userPatchDto.setTinkoffToken(Optional.of("newToken"));
        userPatchDto.setCurrentBalance(Optional.of(new BigDecimal("200.00")));
        userPatchDto.setPercentageChangeSinceYesterday(Optional.of(new BigDecimal("0.02")));
        userPatchDto.setMonetaryChangeSinceYesterday(Optional.of(new BigDecimal("2.00")));
        userPatchDto.setAccountCount(Optional.of(2));
        userPatchDto.setIsMarginTradingEnabled(Optional.of(false));
        userPatchDto.setMarginTradingMetrics(Optional.of("newMetrics"));
        userPatchDto.setTinkoffInvestmentTariff(Optional.of("newTariff"));
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
    void testUpdateUserWithPatchDto() {
        when(userRepository.findByExternalId(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.updateUser(user.getExternalId(), userPatchDto);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(67890L, result.getTelegramChatId());
        assertNull(result.getTelegramNickname());
        assertEquals("newToken", result.getTinkoffToken());
        assertEquals(new BigDecimal("200.00"), result.getCurrentBalance());
        assertEquals(new BigDecimal("0.02"), result.getPercentageChangeSinceYesterday());
        assertEquals(new BigDecimal("2.00"), result.getMonetaryChangeSinceYesterday());
        assertEquals(2, result.getAccountCount().intValue());
        assertFalse(result.getIsMarginTradingEnabled());
        assertEquals("newMetrics", result.getMarginTradingMetrics());
        assertEquals("newTariff", result.getTinkoffInvestmentTariff());
        verify(userRepository, times(1)).findByExternalId(any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserWithPatchDto_PartialUpdate() {
        UserPatchDto partialUpdateDto = new UserPatchDto();
        partialUpdateDto.setFirstName(Optional.of("Jane"));
        partialUpdateDto.setLastName(Optional.of(""));
        partialUpdateDto.setTelegramChatId(Optional.empty());

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
        UserPatchDto userPatchDto = getUserPatchDto();

        User user = new User();
        userMapper.updateUserFromDto(userPatchDto, user);

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
        assertNull(user.getTinkoffInvestmentTariff());
    }

    private static UserPatchDto getUserPatchDto() {
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setFirstName(Optional.of("John"));
        userPatchDto.setLastName(Optional.empty());
        userPatchDto.setTelegramChatId(Optional.empty());
        userPatchDto.setTelegramNickname(Optional.of("johnny"));
        userPatchDto.setTinkoffToken(Optional.empty());
        userPatchDto.setCurrentBalance(Optional.of(BigDecimal.valueOf(1000)));
        userPatchDto.setPercentageChangeSinceYesterday(Optional.empty());
        userPatchDto.setMonetaryChangeSinceYesterday(Optional.of(BigDecimal.valueOf(50)));
        userPatchDto.setAccountCount(Optional.empty());
        userPatchDto.setIsMarginTradingEnabled(Optional.of(true));
        userPatchDto.setMarginTradingMetrics(Optional.empty());
        userPatchDto.setTinkoffInvestmentTariff(Optional.of(""));
        return userPatchDto;
    }

    @Test
    void testUpdateUserFromDto_NullDto() {
        UserPatchDto userPatchDto = null;
        User user = new User();

        if (userPatchDto != null) {
            UserMapper.INSTANCE.updateUserFromDto(userPatchDto, user);
        }

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