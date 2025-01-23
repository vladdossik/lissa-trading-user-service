package lissa.trading.user.service.service.stat;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class UserDailyStatsUpdateEvent extends ApplicationEvent {
    private final List<UUID> userIds;

    public UserDailyStatsUpdateEvent(Object source, List<UUID> userIds) {
        super(source);
        this.userIds = userIds;
    }
}