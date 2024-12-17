package lissa.trading.user.service.service.creation.update;

import lissa.trading.user.service.exception.OperationUnsupportedByBrokerException;
import lissa.trading.user.service.model.User;

public interface UpdateService {
    default void updateUserBalance(User user, String accountId) {
        throw new OperationUnsupportedByBrokerException("User balance update operation unsupported by: "
                + user.getBroker());
    }

    default void updateUserMarginMetrics(User user, String accountId) {
        throw new OperationUnsupportedByBrokerException("User margin metrics update operation unsupported by: "
                + user.getBroker());
    }

    default void updateUserPositions(User user, String accountId) {
        throw new OperationUnsupportedByBrokerException("User positions update operation unsupported by: "
                + user.getBroker());
    }

    default int updateUserAccounts(User user) {
        throw new OperationUnsupportedByBrokerException("User accounts update operation unsupported by: "
                + user.getBroker());
    }

    default void updateUserFavouriteStocks(User user) {
        throw new OperationUnsupportedByBrokerException("User favourite stocks update operation unsupported by: "
                + user.getBroker());
    }
}
