package lissa.trading.user.service.service.publisher;

import lissa.trading.user.service.dto.notification.OperationEnum;
import lissa.trading.user.service.dto.notification.UserFavoriteStocksUpdateDto;
import lissa.trading.user.service.dto.notification.UserUpdateNotificationDto;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserUpdatesPublisherImpl implements UserUpdatesPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationContext notificationContext;
    private final UserMapper userMapper;

    @Value("${integration.rabbit.user-service.exchange.name}")
    private String exchangeName;

    @Value("${integration.rabbit.user-service.favourite-stocks-queue.routing-key}")
    private String favouriteStocksRoutingKey;

    @Value("${integration.rabbit.user-service.user-update-queue.routing-key}")
    private String userUpdateRoutingKey;

    @Override
    public void publishUserUpdateNotification(User user, OperationEnum operationEnum) {
        if (notificationContext.isExternalSource()) {
            log.info("external-source user update, returning");
            return;
        }
        UserUpdateNotificationDto updateDto = userMapper.toUserUpdateNotificationDto(user);
        updateDto.setOperation(operationEnum);
        rabbitTemplate.convertAndSend(exchangeName, userUpdateRoutingKey, updateDto);
        log.info("published user update notification: {}", updateDto);
    }

    @Override
    public void publishUserFavoriteStocksUpdateNotification(User user) {
        rabbitTemplate.convertAndSend(exchangeName, favouriteStocksRoutingKey,
                                      UserFavoriteStocksUpdateDto.builder()
                                              .favoriteStocksEntity(user.getFavoriteStocks())
                                              .externalId(user.getExternalId())
                                              .build());
        log.info("published user favorite stocks update notification for: {}", user);
    }
}
