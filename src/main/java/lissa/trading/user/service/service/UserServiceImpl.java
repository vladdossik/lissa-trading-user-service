package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.UserMapper;
import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    @Transactional
    public UserResponseDto createUser(UserPostDto userPostDto) {
        User user = userMapper.toUser(userPostDto);
        user.setExternalId(UUID.randomUUID().toString());
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(String externalId, UserPostDto userUpdates) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUserFromDto(userUpdates, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void blockUserByTelegramNickname(String telegramNickname) {
        User user = userRepository.findByTelegramNickname(telegramNickname)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsMarginTradingEnabled(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserByExternalId(String externalId) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByExternalId(String externalId) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable, String firstName, String lastName) {
        Specification<User> specification = UserSpecification.withFilters(firstName, lastName);
        Page<User> usersPage = userRepository.findAll(specification, pageable);

        List<UserResponseDto> userResponseDto = usersPage.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(userResponseDto, pageable, usersPage.getTotalElements());
    }
}
