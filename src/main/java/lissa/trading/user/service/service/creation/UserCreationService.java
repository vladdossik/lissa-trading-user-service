package lissa.trading.user.service.service.creation;

import lissa.trading.user.service.model.TempUserReg;

public interface UserCreationService {
    void createUserFromTempUserReg(TempUserReg tempUserReg);
}
