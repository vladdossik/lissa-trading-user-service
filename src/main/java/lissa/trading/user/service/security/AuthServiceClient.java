package lissa.trading.user.service.security;

import lissa.trading.user.service.dto.post.UserAuthInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthServiceClient {

    @PostMapping("/api/auth/user-info")
    UserAuthInfoDto getUserInfo(@RequestHeader("Authorization") String token);
}