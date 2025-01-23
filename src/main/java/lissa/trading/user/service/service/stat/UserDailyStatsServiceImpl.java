package lissa.trading.user.service.service.stat;

import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.projections.UserExternalIdProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDailyStatsServiceImpl implements UserDailyStatsService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void updateAllUsersDailyStats() {
        List<UUID> userIds = userRepository.findAllProjectedBy()
                .stream()
                .map(UserExternalIdProjection::getExternalId)
                .toList();
        eventPublisher.publishEvent(new UserDailyStatsUpdateEvent(this, userIds));
    }
}