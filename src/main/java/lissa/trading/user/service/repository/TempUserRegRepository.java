package lissa.trading.user.service.repository;

import lissa.trading.user.service.model.TempUserReg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TempUserRegRepository extends JpaRepository<TempUserReg, Long>, JpaSpecificationExecutor<TempUserReg> {
    Optional<TempUserReg> findByTelegramNickname(String telegramNickname);

    Optional<TempUserReg> findByExternalId(UUID externalId);
}