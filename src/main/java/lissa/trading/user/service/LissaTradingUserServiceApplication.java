package lissa.trading.user.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = "lissa.trading")
@EnableScheduling
public class LissaTradingUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LissaTradingUserServiceApplication.class, args);
    }

}
