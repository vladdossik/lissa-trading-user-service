package lissa.trading.user.service.service.stat;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.UserDailyStats;
import lissa.trading.user.service.model.entity.BalanceEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.UserDailyStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDailyStatsEventListener {

    private final UserDailyStatsRepository userDailyStatsRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDailyStatsUpdate(UserDailyStatsUpdateEvent event) {
        List<UUID> userIds = event.getUserIds();
        if (CollectionUtils.isEmpty(userIds)) {
            log.warn("No user IDs provided for processing");
            return;
        }

        log.info("Processing users with IDs: {}", userIds);

        List<User> users = userRepository.findAllWithBalancesAndMarginTradingMetricsByExternalIds(userIds);
        if (users.isEmpty()) {
            log.warn("No users found for the provided IDs: {}", userIds);
            return;
        }

        users.forEach(this::processUserStats);
    }

    public void processUserStats(User user) {
        try {
            log.debug("Processing daily stats for user: {}", user.getId());
            BigDecimal currentBalance = getCurrentBalance(user);
            Optional<UserDailyStats> yesterdayStats = getYesterdayStats(user);

            BigDecimal monetaryChange = yesterdayStats.map(stats -> currentBalance.subtract(stats.getBalance()))
                    .orElse(BigDecimal.ZERO);

            BigDecimal percentageChange = yesterdayStats
                    .filter(stats -> stats.getBalance()
                            .compareTo(BigDecimal.ZERO) != 0)
                    .map(stats -> monetaryChange.divide(stats.getBalance(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)))
                    .orElse(BigDecimal.ZERO);

            updateUserStats(user, monetaryChange, percentageChange);
            saveTodayStats(user, currentBalance, monetaryChange, percentageChange);

        } catch (Exception e) {
            log.error("Failed to process stats for user: {}", user.getId(), e);
        }
    }

    private BigDecimal getCurrentBalance(User user) {
        return user.getBalances()
                .stream()
                .map(BalanceEntity::getTotalAmountBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Optional<UserDailyStats> getYesterdayStats(User user) {
        return userDailyStatsRepository.findByUserAndDate(user, OffsetDateTime.now()
                .minusDays(1));
    }

    private void updateUserStats(User user, BigDecimal monetaryChange, BigDecimal percentageChange) {
        user.setMonetaryChangeSinceYesterday(monetaryChange);
        user.setPercentageChangeSinceYesterday(percentageChange);
        userRepository.save(user);
    }

    private void saveTodayStats(User user, BigDecimal currentBalance, BigDecimal monetaryChange,
                                BigDecimal percentageChange) {
        UserDailyStats todayStats = new UserDailyStats();
        todayStats.setUser(user);
        todayStats.setDate(OffsetDateTime.now());
        todayStats.setBalance(currentBalance);
        todayStats.setMonetaryChange(monetaryChange);
        todayStats.setPercentageChange(percentageChange);

        userDailyStatsRepository.save(todayStats);
    }
}


