package lissa.trading.user.service.repository;

import lissa.trading.user.service.model.UserReg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegRepository extends JpaRepository<UserReg, Long> {
}
