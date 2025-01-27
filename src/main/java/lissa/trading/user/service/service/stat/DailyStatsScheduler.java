package lissa.trading.user.service.service.stat;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@EnableScheduling
@RequiredArgsConstructor
public class DailyStatsScheduler {

    private final UserDailyStatsService userDailyStatsService;

    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name = "TaskScheduler_performDailyStatsUpdate",
            lockAtLeastFor = "PT3S", lockAtMostFor = "PT15S")
    public void performDailyStatsUpdate() {
        userDailyStatsService.updateAllUsersDailyStats();
    }
}