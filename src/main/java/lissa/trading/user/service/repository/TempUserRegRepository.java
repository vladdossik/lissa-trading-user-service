package lissa.trading.user.service.repository;

import lissa.trading.user.service.model.TempUserReg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TempUserRegRepository extends JpaRepository<TempUserReg, Long> {
    Optional<TempUserReg> findByExternalId(UUID externalId);
}