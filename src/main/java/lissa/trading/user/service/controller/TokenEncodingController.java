package lissa.trading.user.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lissa.trading.user.service.dto.post.UserAuthInfoDto;
import lissa.trading.user.service.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Token Encoding Controller", description = "API для получения зашифрованного токена")
public class TokenEncodingController {

    @Operation(summary = "Получение зашифрованного токена Tinkoff")
    @ApiResponse(
            description = "Зашифрованный токен успешно получен",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @GetMapping("/tinkoff-token")
    public String getTinkoffToken(@AuthenticationPrincipal UserAuthInfoDto userAuthInfo) {
        if (userAuthInfo == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return userAuthInfo.getTinkoffToken();
    }
}