package lissa.trading.user.service.repository.entity;

import lissa.trading.user.service.model.entity.UserOperationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface UserOperationsEntityRepository extends JpaRepository<UserOperationsEntity, Long> {

    List<UserOperationsEntity> findByUserId(Long userId);

    List<UserOperationsEntity> findByUserIdAndOperationType(Long userId, String operationType);

    List<UserOperationsEntity> findByUserIdAndOperationDateAfter(Long userId, OffsetDateTime operationDate);

    void deleteByUserId(Long userId);
}