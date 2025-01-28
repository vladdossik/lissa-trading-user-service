package lissa.trading.user.service.service.publisher;

import lissa.trading.user.service.dto.notification.OperationEnum;
import lissa.trading.user.service.model.User;

public interface UserUpdatesPublisher {
    void publishUserUpdateNotification(User user, OperationEnum operationEnum);

    void publishUserFavoriteStocksUpdateNotification(User user);
}
