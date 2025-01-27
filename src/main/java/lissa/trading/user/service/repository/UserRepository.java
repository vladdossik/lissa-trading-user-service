package lissa.trading.user.service.repository;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.projections.UserExternalIdProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>,
        JpaSpecificationExecutorWithProjection<User, Long> {
    Optional<User> findByTelegramNickname(String telegramNickname);

    Optional<User> findByExternalId(UUID externalId);

    Boolean existsByTelegramNickname(String telegramNickname);

    @Modifying
    @Query("UPDATE User u SET u.accountCount = :accountCount WHERE u.id = :userId")
    int updateAccountCount(@Param("userId") Long userId, @Param("accountCount") int accountCount);

    @Query(nativeQuery = true, value = "select * from users limit :limit offset :offset")
    List<User> findAllWithLimitAndOffset(int limit, int offset);

    List<UserExternalIdProjection> findAllProjectedBy();

    @Query("SELECT u FROM User u "
            + "LEFT JOIN FETCH u.balances b "
            + "LEFT JOIN FETCH u.marginTradingMetrics m "
            + "WHERE u.externalId IN :ids")
    List<User> findAllWithBalancesAndMarginTradingMetricsByExternalIds(@Param("ids") List<UUID> ids);
}