package lissa.trading.user.service.repository;

import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public interface UserExternalIdProjection {
    @Value("#{target.externalId}")
   UUID getExternalId();
}
