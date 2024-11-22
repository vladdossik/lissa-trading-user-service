package lissa.trading.user.service.service;

import jakarta.validation.Valid;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.dto.response.UserIdsResponseDto;
import lissa.trading.user.service.exception.UserNotFoundException;
import lissa.trading.user.service.mapper.PageMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.page.CustomPage;
import lissa.trading.user.service.repository.UserExternalIdProjection;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.service.publisher.StatsPublisher;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final StatsPublisher<User> statsPublisher;

    @Override
    @Transactional
    public UserResponseDto updateUser(UUID externalId, @Valid UserPatchDto userUpdates) {
        User savedUser = userRepository.save(
                userMapper.updateUserFromDto(userUpdates, findUserByExternalId(externalId)));
        statsPublisher.publishUserData(savedUser);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @Transactional
    public void blockUserByTelegramNickname(String telegramNickname) {
        User user = findUserByTelegramNickname(telegramNickname);
        user.setIsMarginTradingEnabled(false);
        userRepository.save(user);
        log.info("User with Telegram nickname {} blocked", telegramNickname);
    }

    @Override
    @Transactional
    public void deleteUserByExternalId(UUID externalId) {
        userRepository.delete(findUserByExternalId(externalId));
        log.info("User with external ID {} deleted", externalId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByExternalId(UUID externalId) {
        return userMapper.toUserResponseDto(findUserByExternalId(externalId));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable, String firstName,
                                                                        String lastName) {
        log.info("Fetching users with pagination and filters - firstName: {}, lastName: {}", firstName, lastName);

        Specification<User> specification = UserSpecification.withFilters(firstName, lastName);
        Page<User> usersPage = userRepository.findAll(specification, pageable);

        CustomPage<UserResponseDto> customPage = PageMapper.toCustomPage(usersPage, userMapper::toUserResponseDto);

        log.info("Fetched {} users after pagination", customPage.getTotalElements());
        return customPage;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomPage<UserIdsResponseDto> getUserIdsWithPaginationAndFilters(Pageable pageable, String firstName,
                                                                             String lastName) {
        log.info("Fetching user ids with pagination and filters - firstName: {}, lastName: {}", firstName, lastName);
        Specification<User> spec = UserSpecification.withFilters(firstName, lastName);
        List<UserExternalIdProjection> usersPage = userRepository.findBy(spec,
                q -> q.as(UserExternalIdProjection.class).project("externalId").all());
        log.info("Fetched ids: {}", usersPage);

//        CustomPage<UserIdsResponseDto> customPage = PageMapper.toCustomPage(usersPage, userMapper::toUserIdsResponseDto);

        log.info("Fetched {} user ids after pagination", usersPage.size());
        return new CustomPage<UserIdsResponseDto>(null, 0, 0, 0, null);
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