package lissa.trading.user.service.repository.entity;

import lissa.trading.user.service.model.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountEntityRepository extends JpaRepository<UserAccountEntity, Long> {

    List<UserAccountEntity> findByUserId(Long userId);

    Optional<UserAccountEntity> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);

    void deleteByUserId(Long userId);
}