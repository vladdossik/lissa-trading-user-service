package lissa.trading.user.service.service.consumer;

import lissa.trading.user.service.dto.notification.UserFavoriteStocksUpdateDto;
import lissa.trading.user.service.dto.notification.UserUpdateNotificationDto;
import lissa.trading.user.service.mapper.FavoriteStockMapper;
import lissa.trading.user.service.service.publisher.NotificationContext;
import lissa.trading.user.service.mapper.TempUserRegMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.service.UserService;
import lissa.trading.user.service.service.creation.temp.TempUserCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserUpdatesNotificationConsumerImpl implements UserUpdatesNotificationConsumer {

    private final NotificationContext notificationContext;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TempUserCreationService tempUserCreationService;
    private final TempUserRegMapper tempUserRegMapper;
    private final FavoriteStockMapper favoriteStockMapper;
    private final UserRepository userRepository;

    @Value("${integration.rabbit.user-service.user-update-queue.routing-key}")
    private String userUpdateRoutingKey;

    @Value("${integration.rabbit.user-service.favourite-stocks-queue.routing-key}")
    private String userFavoriteStocksRoutingKey;

    @RabbitListener(queues = "${integration.rabbit.user-service.user-update-queue.name}")
    @Override
    public void receiveUserUpdateNotification(UserUpdateNotificationDto userUpdateDto, @Header("amqp_receivedRoutingKey") String routingKey) {
        log.info("received user update notification: {}", userUpdateDto);
        if (routingKey.equals(userUpdateRoutingKey)) {
            return;
        }
        notificationContext.setFromExternalSource(true);
        processUpdateNotification(userUpdateDto);
    }

    @RabbitListener(queues = "${integration.rabbit.user-service.favourite-stocks-queue.name}")
    @Override
    public void receiveUserFavoriteStocksUpdateNotification(UserFavoriteStocksUpdateDto userFavoriteStocksUpdateDto,
                                                            @Header("amqp_receivedRoutingKey") String routingKey) {
        if (routingKey.equals(userFavoriteStocksRoutingKey)) {
            return;
        }
        log.info("received user favorite stocks update notification: {}", userFavoriteStocksUpdateDto);
        notificationContext.setFromExternalSource(true);
        processFavoriteStocksUpdateNotification(userFavoriteStocksUpdateDto);
    }

    private void processUpdateNotification(UserUpdateNotificationDto userUpdate) {
        log.info("processing user update notification: {}", userUpdate);
        switch (userUpdate.getOperation()) {
            case REGISTER:
                registerUser(userUpdate);
                return;
            case UPDATE:
                updateUser(userUpdate);
        }
    }

    private void processFavoriteStocksUpdateNotification(
            UserFavoriteStocksUpdateDto userFavoriteStocksUpdateDto) {
        log.info("processing favorite stocks update notification: {}", userFavoriteStocksUpdateDto);
        userService.setUpdatedFavoriteStocksToUser(
                userFavoriteStocksUpdateDto.getExternalId(),
                favoriteStockMapper.toFavoriteStocksFromNotificationFavoriteStockDto(
                                userFavoriteStocksUpdateDto.getFavoriteStocks()));
    }

    private void registerUser(UserUpdateNotificationDto userUpdate) {
        if (userRepository.existsByTelegramNickname(userUpdate.getTelegramNickname())) {
            log.info("user already exists, updating, {}", userUpdate);
            updateUser(userUpdate);
            return;
        }
        log.info("registering user {}", userUpdate);
        tempUserCreationService.createTempUser(
                tempUserRegMapper.toUserInfoDto(userUpdate));
    }

    private void updateUser(UserUpdateNotificationDto userUpdate) {
        if (!userRepository.existsByTelegramNickname(userUpdate.getTelegramNickname())) {
            log.info("user does not exist, registering, {}", userUpdate);
            registerUser(userUpdate);
            return;
        }
        log.info("updating user {}", userUpdate);
        userService.updateUser(userUpdate.getExternalId(),
                           userMapper.toUserPatchDto(userUpdate));
    }
}
