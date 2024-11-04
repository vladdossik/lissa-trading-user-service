package lissa.trading.user.service.repository;

import lissa.trading.user.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByTelegramNickname(String telegramNickname);

    Optional<User> findByExternalId(UUID externalId);

    @Modifying
    @Query("UPDATE User u SET u.accountCount = :accountCount WHERE u.id = :userId")
    int updateAccountCount(@Param("userId") Long userId, @Param("accountCount") int accountCount);

    @Query(nativeQuery = true, value = "select * from users limit :limit offset :offset")
    List<User> findAllWithLimitAndOffset(int limit, int offset);
}