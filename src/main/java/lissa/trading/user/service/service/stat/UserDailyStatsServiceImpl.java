package lissa.trading.user.service.service.stat;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDailyStatsServiceImpl implements UserDailyStatsService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void updateAllUsersDailyStats() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            eventPublisher.publishEvent(new UserDailyStatsUpdateEvent(this, user));
        }
    }
}