package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.UserPatchDto;
import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.page.CustomPage;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(UserPostDto userPostDto);
    UserResponseDto updateUser(UUID externalId, UserPatchDto userUpdates);
    void blockUserByTelegramNickname(String telegramNickname);
    void deleteUserByExternalId(UUID externalId);
    UserResponseDto getUserByExternalId(UUID externalId);
    CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable, String firstName, String lastName);
}