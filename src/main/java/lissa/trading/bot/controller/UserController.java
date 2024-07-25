package lissa.trading.bot.controller;


import lissa.trading.bot.model.User;
import lissa.trading.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PatchMapping("/{externalId}")
    public ResponseEntity<User> updateUser(@PathVariable String externalId, @RequestBody User userUpdates) {
        User updatedUser = userService.updateUser(externalId, userUpdates);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/block/{telegramNickname}")
    public ResponseEntity<Void> blockUserByTelegramNickname(@PathVariable String telegramNickname) {
        userService.blockUserByTelegramNickname(telegramNickname);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> deleteUserByExternalId(@PathVariable String externalId) {
        userService.deleteUserByExternalId(externalId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<User> getUserByExternalId(@PathVariable String externalId) {
        Optional<User> user = userService.getUserByExternalId(externalId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<User>> getUsersWithPaginationAndFilters(Pageable pageable,
                                                                       @RequestParam(required = false) String firstName,
                                                                       @RequestParam(required = false) String lastName) {
        Page<User> users = userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName);
        return ResponseEntity.ok(users);
    }
}