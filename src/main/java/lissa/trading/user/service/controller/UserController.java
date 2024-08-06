package lissa.trading.user.service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.page.CustomPage;
import lissa.trading.user.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Controller", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Обновление пользователя")
    @PatchMapping("/{externalId}")
    public UserResponseDto updateUser(@PathVariable UUID externalId, @Valid @RequestBody UserPatchDto userUpdates) {
        return userService.updateUser(externalId, userUpdates);
    }

    @Operation(summary = "Блокировка пользователя по Telegram никнейму")
    @PostMapping("/block/{telegramNickname}")
    public void blockUserByTelegramNickname(@PathVariable String telegramNickname) {
        userService.blockUserByTelegramNickname(telegramNickname);
    }

    @Operation(summary = "Удаление пользователя по внешнему идентификатору")
    @DeleteMapping("/{externalId}")
    public void deleteUserByExternalId(@PathVariable UUID externalId) {
        userService.deleteUserByExternalId(externalId);
    }

    @Operation(summary = "Получение пользователя по внешнему идентификатору")
    @GetMapping("/{externalId}")
    public UserResponseDto getUserByExternalId(@PathVariable UUID externalId) {
        return userService.getUserByExternalId(externalId);
    }

    @Operation(summary = "Получение пользователей с пагинацией и фильтрацией")
    @GetMapping
    public CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable,
                                                                        @RequestParam(required = false) String firstName,
                                                                        @RequestParam(required = false) String lastName) {
        return userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName);
    }
}