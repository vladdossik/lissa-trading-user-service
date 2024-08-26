package lissa.trading.user.service.service.creation;


import lissa.trading.lissa.auth.lib.dto.UserInfoDto;

public interface TempUserCreationService {
    void createTempUser(UserInfoDto userInfoDto);
}
