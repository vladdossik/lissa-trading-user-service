package lissa.trading.user.service.controller;

import jakarta.validation.Valid;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.post.TempUserRegPostDto;
import lissa.trading.user.service.dto.post.UserPostDto;
import lissa.trading.user.service.dto.response.TempUserRegResponseDto;
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
public class UserController {

    private final UserService userService;

    @PostMapping("/temp")
    public TempUserRegResponseDto createTempUser(@Valid @RequestBody TempUserRegPostDto tempUserRegPostDto) {
        return userService.createTempUser(tempUserRegPostDto);
    }

    @PostMapping("/temp/{externalId}/finalize")
    public UserResponseDto createUserFromTempUser(@PathVariable UUID externalId, @Valid @RequestBody UserPostDto userPostDto) {
        return userService.createUserFromTempUser(externalId, userPostDto);
    }

    @PatchMapping("/{externalId}")
    public UserResponseDto updateUser(@PathVariable UUID externalId, @Valid @RequestBody UserPatchDto userUpdates) {
        return userService.updateUser(externalId, userUpdates);
    }

    @PostMapping("/block/{telegramNickname}")
    public void blockUserByTelegramNickname(@PathVariable String telegramNickname) {
        userService.blockUserByTelegramNickname(telegramNickname);
    }

    @DeleteMapping("/{externalId}")
    public void deleteUserByExternalId(@PathVariable UUID externalId) {
        userService.deleteUserByExternalId(externalId);
    }

    @GetMapping("/{externalId}")
    public UserResponseDto getUserByExternalId(@PathVariable UUID externalId) {
        return userService.getUserByExternalId(externalId);
    }

    @GetMapping
    public CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable,
                                                                        @RequestParam(required = false) String firstName,
                                                                        @RequestParam(required = false) String lastName) {
        return userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName);
    }
}

