package lissa.trading.user.service.service.creation;

import lissa.trading.user.service.exception.OperationUnsupportedByBrokerException;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.service.creation.factory.SupportedBrokersEnum;

import java.util.concurrent.ThreadLocalRandom;

public interface UserCreationService {

    void createUserFromTempUserReg(TempUserReg tempUserReg);

    SupportedBrokersEnum getBroker();

    default void updateUser(User user) {
        throw new OperationUnsupportedByBrokerException("User update operation unsupported by: " + user.getBroker());
    }

    default void getTelegramInfo(User user) {
        if (user.getTelegramChatId() == null) {
            user.setTelegramChatId(ThreadLocalRandom.current().nextLong());
        }
    }
}
