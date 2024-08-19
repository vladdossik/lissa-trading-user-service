package lissa.trading.user.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lissa.trading.user.service.dto.post.UserAuthInfoDto;
import lissa.trading.user.service.exception.UnauthorizedException;
import lissa.trading.user.service.service.FirstInteractionUserReg;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Registration Controller", description = "API для управления пользователями")
public class UserRegController {

    private final FirstInteractionUserReg firstInteractionUserReg;

    @Operation(summary = "Регистрация пользователя по токену")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно зарегистрирован",
            content = @Content(schema = @Schema(implementation = Void.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Пользователь не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping("/register")
    public void registerUser(@AuthenticationPrincipal UserAuthInfoDto userAuthInfo) {
        if (userAuthInfo == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        firstInteractionUserReg.createTempUser(userAuthInfo);
    }
}