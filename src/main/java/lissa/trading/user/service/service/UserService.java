package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.post.TempUserRegPostDto;
import lissa.trading.user.service.dto.post.UserPostDto;
import lissa.trading.user.service.dto.response.TempUserRegResponseDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.page.CustomPage;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    TempUserRegResponseDto createTempUser(TempUserRegPostDto tempUserRegPostDto);
    UserResponseDto createUserFromTempUser(UUID externalId, UserPostDto userPostDto);
    UserResponseDto updateUser(UUID externalId, UserPatchDto userUpdates);
    void blockUserByTelegramNickname(String telegramNickname);
    void deleteUserByExternalId(UUID externalId);
    UserResponseDto getUserByExternalId(UUID externalId);
    CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable, String firstName, String lastName);
}
