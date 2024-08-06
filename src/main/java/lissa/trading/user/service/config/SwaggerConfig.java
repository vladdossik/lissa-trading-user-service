package lissa.trading.user.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API для управления пользователями и временными пользователями")
                        .description("Этот API предоставляет методы для управления пользователями, включая обновление, блокировку, удаление и получение информации о пользователях. Также доступны методы управления временными пользователями.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Belaquaa")
                                .url("https://t.me/belaquaa"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/api/users/**")
                .build();
    }
}