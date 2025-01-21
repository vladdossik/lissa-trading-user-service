package lissa.trading.user.service.service.stat;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.UserDailyStats;
import lissa.trading.user.service.model.entity.BalanceEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.UserDailyStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UserDailyStatsEventListener {

    private final UserDailyStatsRepository userDailyStatsRepository;
    private final UserRepository userRepository;

    @EventListener
    public void handleUserDailyStatsUpdate(UserDailyStatsUpdateEvent event) {
        User user = event.getUser();
        BigDecimal currentBalance = getCurrentBalance(user);

        UserDailyStats yesterdayStats = userDailyStatsRepository
                .findByUserAndDate(user, OffsetDateTime.now().minusDays(1))
                .orElse(null);

        BigDecimal monetaryChange = BigDecimal.ZERO;
        BigDecimal percentageChange = BigDecimal.ZERO;

        if (yesterdayStats != null) {
            BigDecimal previousBalance = yesterdayStats.getBalance();
            monetaryChange = currentBalance.subtract(previousBalance);
            percentageChange = previousBalance.compareTo(BigDecimal.ZERO) != 0
                    ? monetaryChange.divide(previousBalance, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
        }

        user.setMonetaryChangeSinceYesterday(monetaryChange);
        user.setPercentageChangeSinceYesterday(percentageChange);
        userRepository.save(user);

        UserDailyStats todayStats = new UserDailyStats();
        todayStats.setUser(user);
        todayStats.setDate(OffsetDateTime.now());
        todayStats.setBalance(currentBalance);
        todayStats.setMonetaryChange(monetaryChange);
        todayStats.setPercentageChange(percentageChange);

        userDailyStatsRepository.save(todayStats);
    }

    private BigDecimal getCurrentBalance(User user) {
        return user.getBalances().stream()
                .map(BalanceEntity::getTotalAmountBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
