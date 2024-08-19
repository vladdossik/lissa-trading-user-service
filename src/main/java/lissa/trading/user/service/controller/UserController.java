package lissa.trading.user.service.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.page.CustomPage;
import lissa.trading.user.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Получение пользователей с пагинацией и фильтрацией")
    @ApiResponse(
            description = "Пользователи успешно получены с пагинацией и фильтрацией",
            content = @Content(schema = @Schema(implementation = CustomPage.class))
    )
    @GetMapping
    @PreAuthorize("hasRole('VIP') or hasRole('ADMIN')")
    public CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable,
                                                                        @RequestParam(required = false) String firstName,
                                                                        @RequestParam(required = false) String lastName) {
        return userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName);
    }

    @Operation(summary = "Получение пользователя по внешнему идентификатору")
    @ApiResponse(
            description = "Пользователь успешно получен по внешнему идентификатору",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
    )
    @GetMapping("/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto getUserByExternalId(@PathVariable UUID externalId) {
        return userService.getUserByExternalId(externalId);
    }

    @Operation(summary = "Обновление пользователя")
    @ApiResponse(
            description = "Пользователь успешно обновлен",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
    )
    @PatchMapping("/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto updateUser(@PathVariable UUID externalId, @Valid @RequestBody UserPatchDto userUpdates) {
        return userService.updateUser(externalId, userUpdates);
    }

    @Operation(summary = "Удаление пользователя по внешнему идентификатору")
    @ApiResponse(
            description = "Пользователь успешно удален",
            content = @Content(schema = @Schema(implementation = Void.class))
    )
    @DeleteMapping("/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserByExternalId(@PathVariable UUID externalId) {
        userService.deleteUserByExternalId(externalId);
    }

    @Operation(summary = "Блокировка пользователя по Telegram никнейму")
    @ApiResponse(
            description = "Пользователь успешно заблокирован",
            content = @Content(schema = @Schema(implementation = Void.class))
    )
    @PostMapping("/block/{telegramNickname}")
    @PreAuthorize("hasRole('ADMIN')")
    public void blockUserByTelegramNickname(@PathVariable String telegramNickname) {
        userService.blockUserByTelegramNickname(telegramNickname);
    }
}