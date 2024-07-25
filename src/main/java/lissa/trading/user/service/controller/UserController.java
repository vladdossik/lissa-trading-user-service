package lissa.trading.user.service.controller;

import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserPostDto userPostDto) {
        return userService.createUser(userPostDto);
    }

    @PatchMapping("/{externalId}")
    public UserResponseDto updateUser(@PathVariable String externalId, @RequestBody UserPostDto userUpdates) {
        return userService.updateUser(externalId, userUpdates);
    }

    @PostMapping("/block/{telegramNickname}")
    public void blockUserByTelegramNickname(@PathVariable String telegramNickname) {
        userService.blockUserByTelegramNickname(telegramNickname);
    }

    @DeleteMapping("/{externalId}")
    public void deleteUserByExternalId(@PathVariable String externalId) {
        userService.deleteUserByExternalId(externalId);
    }

    @GetMapping("/{externalId}")
    public UserResponseDto getUserByExternalId(@PathVariable String externalId) {
        return userService.getUserByExternalId(externalId);
    }

    @GetMapping
    public Page<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable,
                                                                  @RequestParam(required = false) String firstName,
                                                                  @RequestParam(required = false) String lastName) {
        return userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName);
    }
}
