package lissa.trading.user.service.service.dataInitializer;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.UserDailyStats;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.UserDailyStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class DailyStatsInitializerService implements DataInitializerService {

    private final UserRepository userRepository;
    private final UserDailyStatsRepository userDailyStatsRepository;
    private final EasyRandom easyRandom;

    @Override
    public void createData() {
        if (userDailyStatsRepository.count() == 0) {
            for (User user : userRepository.findAll()) {
                UserDailyStats userDailyStats = easyRandom.nextObject(UserDailyStats.class);
                userDailyStats.setUser(user);
                userDailyStatsRepository.save(userDailyStats);
            }
            log.info("Daily stats successfully initialized");
        } else {
            log.info("Daily stats already initialized");
        }
    }
}
