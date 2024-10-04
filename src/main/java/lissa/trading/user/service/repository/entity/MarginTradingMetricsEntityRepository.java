package lissa.trading.user.service.repository.entity;

import lissa.trading.user.service.model.entity.MarginTradingMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarginTradingMetricsEntityRepository extends JpaRepository<MarginTradingMetricsEntity, Long> {

    Optional<MarginTradingMetricsEntity> findByUserId(Long userId);

    Optional<MarginTradingMetricsEntity> findByUserIdAndCurrency(Long userId, String currency);

    void deleteByUserId(Long userId);
}