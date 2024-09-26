package lissa.trading.user.service.service.stat;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class DailyStatsScheduler {

    private final UserDailyStatsService userDailyStatsService;

    @Scheduled(cron = "0 0 0 * * *")  // Каждый день в полночь
    public void performDailyStatsUpdate() {
        userDailyStatsService.updateAllUsersDailyStats();
    }
}