package lissa.trading.user.service.service.consumer;

import lissa.trading.user.service.dto.notification.UserUpdateNotificationDto;
import org.springframework.messaging.handler.annotation.Header;

public interface UserUpdatesNotificationConsumer {
    void receiveUserUpdateNotification(UserUpdateNotificationDto userUpdate, @Header("amqp_receivedRoutingKey") String routingKey);
}
