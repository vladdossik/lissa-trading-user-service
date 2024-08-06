package lissa.trading.user.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lissa.trading.user.service.dto.post.TempUserRegPostDto;
import lissa.trading.user.service.dto.response.TempUserRegResponseDto;
import lissa.trading.user.service.service.TempUserRegService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Internal Controller", description = "API для управления временными пользователями")
public class InternalController {

    private final TempUserRegService tempUserRegService;

    @Operation(summary = "Создание временного пользователя")
    @PostMapping("/temp")
    public TempUserRegResponseDto createTempUser(@Valid @RequestBody TempUserRegPostDto tempUserRegPostDto) {
        return tempUserRegService.createTempUser(tempUserRegPostDto);
    }
}