package lissa.trading.bot.repository;

import lissa.trading.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelegramNickname(String telegramNickname);

    Optional<User> findByExternalId(String externalId);
}