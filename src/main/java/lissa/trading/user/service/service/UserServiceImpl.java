package lissa.trading.user.service.service;

import jakarta.validation.Valid;
import lissa.trading.user.service.dto.UserPatchDto;
import lissa.trading.user.service.mapper.PageMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.exception.UserNotFoundException;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.page.CustomPage;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    @Transactional
    public UserResponseDto createUser(@Valid UserPostDto userPostDto) {
        log.info("Creating user with details: {}", userPostDto);
        User user = userMapper.toUser(userPostDto);
        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UUID externalId, @Valid UserPatchDto userUpdates) {
        log.info("Updating user with external ID: {}", externalId);
        User user = findUserByExternalId(externalId);
        userMapper.updateUserFromDto(userUpdates, user);
        User updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", updatedUser.getId());
        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void blockUserByTelegramNickname(String telegramNickname) {
        log.info("Blocking user with Telegram nickname: {}", telegramNickname);
        User user = findUserByTelegramNickname(telegramNickname);
        user.setIsMarginTradingEnabled(false);
        userRepository.save(user);
        log.info("User with Telegram nickname {} blocked", telegramNickname);
    }

    @Override
    @Transactional
    public void deleteUserByExternalId(UUID externalId) {
        log.info("Deleting user with external ID: {}", externalId);
        User user = findUserByExternalId(externalId);
        userRepository.delete(user);
        log.info("User with external ID {} deleted", externalId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByExternalId(UUID externalId) {
        log.info("Fetching user with external ID: {}", externalId);
        User user = findUserByExternalId(externalId);
        log.info("User fetched with external ID: {}", externalId);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable, String firstName, String lastName) {
        log.info("Fetching users with pagination and filters - firstName: {}, lastName: {}", firstName, lastName);
        Specification<User> specification = UserSpecification.withFilters(firstName, lastName);
        Page<User> usersPage = userRepository.findAll(specification, pageable);

        List<UserResponseDto> userResponseDto = usersPage.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());

        log.info("Fetched {} users before deduplication", userResponseDto.size());

        List<UserResponseDto> distinctUsers = userResponseDto.stream().distinct().collect(Collectors.toList());
        log.info("After removing duplicates, {} users remained", distinctUsers.size());

        CustomPage<UserResponseDto> customPage = new CustomPage<>(
                distinctUsers,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.isLast()
        );

        Page<UserResponseDto> result = PageMapper.INSTANCE.toPage(customPage, pageable);
        log.info("Fetched {} users after pagination", result.getTotalElements());
        return result;
    }

    private User findUserByExternalId(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UserNotFoundException("User with external id " + externalId + " not found"));
    }

    private User findUserByTelegramNickname(String telegramNickname) {
        return userRepository.findByTelegramNickname(telegramNickname)
                .orElseThrow(() -> new UserNotFoundException("User with Telegram nickname " + telegramNickname + " not found"));
    }
}