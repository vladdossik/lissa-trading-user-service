package lissa.trading.user.service.repository;

import lissa.trading.user.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByTelegramNickname(String telegramNickname);

    Optional<User> findByExternalId(String externalId);
}