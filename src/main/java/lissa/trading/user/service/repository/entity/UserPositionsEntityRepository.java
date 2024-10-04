package lissa.trading.user.service.repository.entity;

import lissa.trading.user.service.model.entity.UserPositionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPositionsEntityRepository extends JpaRepository<UserPositionsEntity, Long> {

    List<UserPositionsEntity> findByUserId(Long userId);

    Optional<UserPositionsEntity> findByUserIdAndFigi(Long userId, String figi);

    // Проверить, существует ли позиция с определённым FIGI для пользователя
    boolean existsByUserIdAndFigi(Long userId, String figi);

    void deleteByUserId(Long userId);
}