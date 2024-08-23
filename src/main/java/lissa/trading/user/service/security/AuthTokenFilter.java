package lissa.trading.user.service.security;

import lissa.trading.auth_security_lib.dto.UserInfoDto;
import lissa.trading.auth_security_lib.feign.AuthServiceClient;
import lissa.trading.auth_security_lib.security.BaseAuthTokenFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends BaseAuthTokenFilter {

    private final AuthServiceClient authServiceClient;

    @Override
    protected List<String> parseRoles(Object userInfo) {
        return ((UserInfoDto) userInfo).getRoles();
    }

    @Override
    protected Object retrieveUserInfo(String token) {
        return authServiceClient.getUserInfo("Bearer " + token);
    }
}