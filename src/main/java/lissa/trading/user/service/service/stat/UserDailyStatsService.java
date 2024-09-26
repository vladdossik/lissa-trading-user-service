package lissa.trading.user.service.service.stat;

import lissa.trading.user.service.model.User;

public interface UserDailyStatsService {
    public void updateAllUsersDailyStats();

    public void updateDailyStats(User user);
}
