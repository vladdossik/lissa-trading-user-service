package lissa.trading.user.service.repository;

import lissa.trading.user.service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    @Query("SELECT u.externalId FROM User u WHERE (:firstName IS NULL OR u.firstName = :firstName) AND (:lastName " +
            "IS NULL OR u.lastName = :lastName)")
    Page<UUID> findExternalIdsWithPaginationAndFilters(String firstName, String lastName, Pageable pageable);
}