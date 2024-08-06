package lissa.trading.user.service.service;

import lissa.trading.user.service.model.TempUserReg;

public interface UserCreationService {
    void createUserFromTempUserReg(TempUserReg tempUserReg);
}
