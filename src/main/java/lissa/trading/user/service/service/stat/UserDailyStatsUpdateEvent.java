package lissa.trading.user.service.service.stat;

import lissa.trading.user.service.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserDailyStatsUpdateEvent extends ApplicationEvent {
    private final User user;

    public UserDailyStatsUpdateEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}