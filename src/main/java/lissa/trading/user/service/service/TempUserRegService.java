package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.post.UserAuthInfoDto;

public interface TempUserRegService {
    void createTempUser(UserAuthInfoDto userAuthInfo);
}
