package lissa.trading.user.service.service.publisher;

import lissa.trading.user.service.dto.notification.NotificationFavouriteStockDto;
import lissa.trading.user.service.dto.notification.OperationEnum;
import lissa.trading.user.service.dto.notification.UserFavoriteStocksUpdateDto;
import lissa.trading.user.service.dto.notification.UserUpdateNotificationDto;
import lissa.trading.user.service.mapper.FavoriteStockMapper;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.service.consumer.NotificationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserUpdatesPublisherImpl implements UserUpdatesPublisher {

    @Value("${integration.rabbit.outbound.user-notifications.exchange}")
    private String exchangeName;

    @Value("${integration.rabbit.outbound.user-service.user-update.routing-key}")
    private String userServiceUpdateQueueRoutingKey;

    @Value("${integration.rabbit.outbound.user-service.favourite-stocks.routing-key}")
    private String userServiceFavouriteStocksQueueRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    private final NotificationContext notificationContext;
    private final UserMapper userMapper;
    private final FavoriteStockMapper favoriteStockMapper;

    @Override
    public void publishUserUpdateNotification(User user, OperationEnum operationEnum) {
        if (notificationContext.isExternalSource()) {
            return;
        }
        UserUpdateNotificationDto updateDto = userMapper.toUserUpdateNotificationDto(user);
        updateDto.setOperation(operationEnum);
        rabbitTemplate.convertAndSend(exchangeName,
                                      userServiceUpdateQueueRoutingKey,
                                      updateDto);
        log.info("published user update notification for: {}", user.getExternalId());
    }

    @Override
    public void publishUserFavoriteStocksUpdateNotification(User user) {
        List<NotificationFavouriteStockDto> notificationFavouriteStockDtoList = favoriteStockMapper
                .toNotificationFavouriteStockDtoList(user.getFavoriteStocks());
        log.info("favorite stock size = {}", notificationFavouriteStockDtoList.size());
        rabbitTemplate.convertAndSend(exchangeName, userServiceFavouriteStocksQueueRoutingKey,
                                      UserFavoriteStocksUpdateDto.builder()
                                              .favoriteStocks(notificationFavouriteStockDtoList)
                                              .externalId(user.getExternalId())
                                              .build());
        log.info("published user favorite stocks update notification for: {}", user.getExternalId());
    }
}
