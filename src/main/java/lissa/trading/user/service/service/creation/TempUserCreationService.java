package lissa.trading.user.service.service.creation;

import lissa.trading.auth_security_lib.dto.UserInfoDto;

public interface TempUserCreationService {
    void createTempUser(UserInfoDto userInfoDto);
}
