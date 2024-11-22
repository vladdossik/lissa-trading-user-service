package lissa.trading.user.service.service;

import jakarta.validation.Valid;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.dto.response.UserIdsResponseDto;
import lissa.trading.user.service.page.CustomPage;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponseDto updateUser(UUID externalId, @Valid UserPatchDto userUpdates);

    void blockUserByTelegramNickname(String telegramNickname);

    void deleteUserByExternalId(UUID externalId);

    UserResponseDto getUserByExternalId(UUID externalId);

    CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable, String firstName, String lastName);

    CustomPage<UserIdsResponseDto> getUserIdsWithPaginationAndFilters(Pageable pageable, String firstName,
                                                                      String lastName);
}