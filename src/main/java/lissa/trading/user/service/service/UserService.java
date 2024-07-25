package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto createUser(UserPostDto userPostDto);
    UserResponseDto updateUser(String externalId, UserPostDto userUpdates);
    void blockUserByTelegramNickname(String telegramNickname);
    void deleteUserByExternalId(String externalId);
    UserResponseDto getUserByExternalId(String externalId);
    Page<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable, String firstName, String lastName);
}