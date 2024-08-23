package lissa.trading.user.service.service.creation;

import lissa.trading.user.service.dto.post.UserInfoDto;

public interface TempUserCreationService {
    void createTempUser(UserInfoDto userInfoDto);
}
