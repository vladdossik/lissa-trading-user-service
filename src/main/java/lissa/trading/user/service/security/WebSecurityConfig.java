package lissa.trading.user.service.security;

import lissa.trading.lissa.auth.lib.dto.UserInfoDto;
import lissa.trading.lissa.auth.lib.security.BaseAuthTokenFilter;
import lissa.trading.lissa.auth.lib.security.BaseWebSecurityConfig;
import lissa.trading.user.service.security.internal.InternalTokenFilter;
import lissa.trading.user.service.security.internal.InternalTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig extends BaseWebSecurityConfig {
    private final InternalTokenService internalTokenService;

    public WebSecurityConfig(BaseAuthTokenFilter<UserInfoDto> authTokenFilter, InternalTokenService internalTokenService) {
        super(authTokenFilter);
        this.internalTokenService = internalTokenService;
    }

    @Bean
    public InternalTokenFilter internalTokenFilter() {
        return new InternalTokenFilter(internalTokenService);
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.addFilterBefore(internalTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}