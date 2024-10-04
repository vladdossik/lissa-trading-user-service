package lissa.trading.user.service.repository.entity;

import lissa.trading.user.service.model.entity.BalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BalanceEntityRepository extends JpaRepository<BalanceEntity, Long> {

    List<BalanceEntity> findByUserId(Long userId);

    Optional<BalanceEntity> findByCurrencyAndUserId(String currency, Long userId);

    void deleteByUserId(Long userId);
}