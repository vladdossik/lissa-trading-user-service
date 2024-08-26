package lissa.trading.user.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.user.service.exception.UnauthorizedException;
import lissa.trading.user.service.service.creation.TempUserCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Registration Controller", description = "API для регистрации пользователя")
public class UserRegController {

    private final TempUserCreationService tempUserCreationService;

    @Operation(summary = "Регистрация пользователя по токену")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно зарегистрирован",
            content = @Content()
    )
    @ApiResponse(
            responseCode = "401",
            description = "Пользователь не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@AuthenticationPrincipal UserInfoDto userInfo) {
        if (userInfo == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        tempUserCreationService.createTempUser(userInfo);
        return ResponseEntity.ok("User registration successful");
    }
}