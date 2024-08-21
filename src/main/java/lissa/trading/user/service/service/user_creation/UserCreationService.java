package lissa.trading.user.service.service.user_creation;

import lissa.trading.user.service.model.TempUserReg;

public interface UserCreationService {
    void createUserFromTempUserReg(TempUserReg tempUserReg);
}
