package lissa.trading.user.service.security;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthServiceClient {

    @PostMapping("/api/auth/validate")
    boolean validateToken(@RequestHeader("Authorization") String token);

    @PostMapping("/api/auth/roles")
    List<String> getUserRoles(@RequestHeader("Authorization") String token);
}