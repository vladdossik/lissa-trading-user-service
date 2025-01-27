package lissa.trading.user.service.service.consumer;

import lissa.trading.user.service.dto.notification.UserFavoriteStocksUpdateDto;
import lissa.trading.user.service.dto.notification.UserUpdateNotificationDto;

public interface UserUpdatesNotificationConsumer {
    void receiveUserUpdateNotification(UserUpdateNotificationDto userUpdate);

    void receiveUserFavoriteStocksUpdateNotification(UserFavoriteStocksUpdateDto userFavoriteStocksUpdateDto);
}
