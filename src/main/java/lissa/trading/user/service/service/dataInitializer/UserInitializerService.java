package lissa.trading.user.service.service.dataInitializer;

import lissa.trading.lissa.auth.lib.security.EncryptionService;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
@Order(1)
public class UserInitializerService implements DataInitializerService {

    private final UserRepository userRepository;
    private final EasyRandom easyRandom;

    @Override
    public void createData() {
        if (userRepository.count() == 0) {
            for (int i = 0; i < 10; i++) {
                User user = easyRandom.nextObject(User.class);

                user.getMarginTradingMetrics().setUser(user);
                user.setTinkoffToken(generateTinkoffToken());

                userRepository.save(user);
            }
            log.info("Users successfully initialized");
        } else {
            log.info("Users already initialized");
        }
    }

    private String generateTinkoffToken() {
        StringBuilder token = new StringBuilder("t.");
        for (int i = 0; i < 86; i++) {
            token.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(
                    (int) (Math.random() * 62)
            ));
        }
        return EncryptionService.encrypt(String.valueOf(token));
    }
}
