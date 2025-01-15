package lissa.trading.user.service.service.creation;

import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;

import java.util.concurrent.ThreadLocalRandom;

public interface UserCreationService {

    void createUserFromTempUserReg(TempUserReg tempUserReg);

    default void getTelegramInfo(User user) {
        if (user.getTelegramChatId() == null) {
            user.setTelegramChatId(ThreadLocalRandom.current().nextLong());
        }
    }
}
