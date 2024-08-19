package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.post.UserAuthInfoDto;

public interface FirstInteractionUserReg {
    void createTempUser(UserAuthInfoDto userAuthInfoDto);
}
