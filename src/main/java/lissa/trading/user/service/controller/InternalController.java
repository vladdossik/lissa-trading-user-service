package lissa.trading.user.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lissa.trading.lissa.auth.lib.dto.UpdateTinkoffTokenResponce;
import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.user.service.dto.patch.UserPatchDto;
import lissa.trading.user.service.dto.response.UserResponseDto;
import lissa.trading.user.service.dto.tinkoff.account.TinkoffTokenDto;
import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import lissa.trading.user.service.exception.UnauthorizedException;
import lissa.trading.user.service.feign.tinkoff.TinkoffAccountClient;
import lissa.trading.user.service.page.CustomPage;
import lissa.trading.user.service.service.UserService;
import lissa.trading.user.service.service.creation.temp.TempUserCreationService;
import lissa.trading.user.service.service.publisher.stats.StatsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/internal")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Internal Controller", description = "API для управления пользователями")
public class InternalController {

    private final TempUserCreationService tempUserCreationService;
    private final UserService userService;
    private final StatsPublisher statsPublisher;
    TinkoffAccountClient tinkoffAccountClient;

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
    @ApiResponse(
            responseCode = "206",
            description = "Пользователь успешно зарегистрирован, но" +
                    " брокер не поддерживает получение данных",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserInfoDto userInfo) {
        if (userInfo == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        tempUserCreationService.createTempUser(userInfo);
        return ResponseEntity.ok("User registration successful");
    }

    @Operation(summary = "Отправка данных по всем пользователям в сервис статистики")
    @ApiResponse(
            description = "Данные успешно отправлены"
    )
    @PostMapping("/export-user-data-to-stats-service")
    public void sendUserStats() {
        statsPublisher.publishAllUsersData();
    }

    @Operation(summary = "Получение пользователей с пагинацией и фильтрацией")
    @ApiResponse(
            description = "Пользователи успешно получены с пагинацией и фильтрацией",
            content = @Content(schema = @Schema(implementation = CustomPage.class))
    )
    @GetMapping("/get-users")
    public CustomPage<UserResponseDto> getUsersWithPaginationAndFilters(Pageable pageable,
                                                                        @RequestParam(required = false) String firstName,
                                                                        @RequestParam(required = false) String lastName) {
        return userService.getUsersWithPaginationAndFilters(pageable, firstName, lastName);
    }

    @Operation(summary = "Получение external id пользователей с пагинацией и фильтрацией")
    @ApiResponse(
            description = "External id успешно получены с пагинацией и фильтрацией",
            content = @Content(schema = @Schema(implementation = CustomPage.class))
    )
    @GetMapping("/get-user-ids")
    public List<UUID> getUserIdsWithPaginationAndFilters(Pageable pageable,
                                                         @RequestParam(required = false)
                                                         String firstName,
                                                         @RequestParam(required = false)
                                                         String lastName) {
        return userService.getUserIdsWithPaginationAndFilters(pageable, firstName, lastName);
    }

    @Operation(summary = "Получение пользователя по внешнему идентификатору")
    @ApiResponse(
            description = "Пользователь успешно получен по внешнему идентификатору",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
    )
    @GetMapping("/{externalId}")
    public UserResponseDto getUserByExternalId(@PathVariable UUID externalId) {
        return userService.getUserByExternalId(externalId);
    }

    @Operation(summary = "Удаление пользователя по внешнему идентификатору")
    @ApiResponse(
            description = "Пользователь успешно удален",
            content = @Content()
    )
    @DeleteMapping("/{externalId}")
    public void deleteUserByExternalId(@PathVariable UUID externalId) {
        userService.deleteUserByExternalId(externalId);
    }

    @Operation(summary = "Блокировка пользователя по Telegram никнейму")
    @ApiResponse(
            description = "Пользователь успешно заблокирован",
            content = @Content()
    )
    @PostMapping("/block/{telegramNickname}")
    public void blockUserByTelegramNickname(@PathVariable String telegramNickname) {
        userService.blockUserByTelegramNickname(telegramNickname);
    }

    @PostMapping("/setTinkoffToken")
    public UpdateTinkoffTokenResponce setTinkoffToken(@RequestBody TinkoffTokenDto tinkoffToken) {
        return tinkoffAccountClient.setTinkoffToken(tinkoffToken);
    }

    @Operation(summary = "Обновить или добавить любимые акции пользователя")
    @ApiResponse(
            description = "Любимые акции пользователя успешно обновлены",
            responseCode = "200",
            content = @Content()
    )
    @PostMapping("/update/favouriteStocks/{externalId}")
    public ResponseEntity<String> updateUserFavoriteStocks(@PathVariable UUID externalId, @RequestBody TickersDto tickersDto) {
        userService.updateFavoriteStocks(externalId, tickersDto);
        return ResponseEntity.ok("Successful user favorite stocks update");
    }

    @Operation(summary = "Получить обновленные цены на любимые акции пользователя")
    @ApiResponse(
            description = "Цены на любимые акции успешно получены",
            content = @Content(contentSchema = @Schema(implementation = StocksPricesDto.class))
    )
    @GetMapping("/update/favouriteStocksPrices/{externalId}")
    public StocksPricesDto getUpdatedFavouriteStocksPrices(@PathVariable UUID externalId) {
        return userService.getUpdateOnStockPrices(externalId);
    }
}
