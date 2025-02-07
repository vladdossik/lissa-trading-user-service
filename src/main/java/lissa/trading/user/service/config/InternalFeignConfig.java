package lissa.trading.user.service.config;

import feign.RequestInterceptor;
import lissa.trading.user.service.feign.InternalTokenFeignInterceptor;
import lissa.trading.user.service.security.internal.InternalTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class InternalFeignConfig {

    private final InternalTokenService tokenService;

    @Bean
    public RequestInterceptor internalTokenInterceptor() {
        return new InternalTokenFeignInterceptor(tokenService);
    }
}
