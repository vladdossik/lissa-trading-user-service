package lissa.trading.user.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LissaTradingUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LissaTradingUserServiceApplication.class, args);
    }

}
