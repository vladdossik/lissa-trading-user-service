package lissa.trading.user.service.service.creation.update;

import lissa.trading.user.service.exception.OperationUnsupportedByBrokerException;
import lissa.trading.user.service.model.User;

public interface UpdateService {
    default void updateUserBalance(User user, String accountId) {
        throw new OperationUnsupportedByBrokerException("User update operation unsupported by: "
                + user.getBroker());
    }

    default void updateUserMarginMetrics(User user, String accountId) {
        throw new OperationUnsupportedByBrokerException("User update operation unsupported by: "
                + user.getBroker());
    }

    default void updateUserPositions(User user, String accountId) {
        throw new OperationUnsupportedByBrokerException("User update operation unsupported by: "
                + user.getBroker());
    }

    default int updateUserAccounts(User user) {
        throw new OperationUnsupportedByBrokerException("User update operation unsupported by: "
                + user.getBroker());
    }

    default void updateUserFavouriteStocks(User user) {
        throw new OperationUnsupportedByBrokerException("User update operation unsupported by: "
                + user.getBroker());
    }
}
