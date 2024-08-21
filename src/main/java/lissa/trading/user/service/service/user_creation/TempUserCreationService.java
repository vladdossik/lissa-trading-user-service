package lissa.trading.user.service.service.user_creation;

import lissa.trading.user.service.dto.post.UserInfoDto;

public interface TempUserCreationService {
    void createTempUser(UserInfoDto userInfoDto);
}
