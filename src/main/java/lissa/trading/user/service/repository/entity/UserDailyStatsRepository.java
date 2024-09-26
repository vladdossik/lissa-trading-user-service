package lissa.trading.user.service.repository.entity;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.UserDailyStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserDailyStatsRepository extends JpaRepository<UserDailyStats, Long> {
    Optional<UserDailyStats> findByUserAndDate(User user, OffsetDateTime date);
}