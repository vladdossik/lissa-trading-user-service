package lissa.trading.user.service.event;

import lissa.trading.user.service.dto.post.UserInfoDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserAuthenticatedEvent extends ApplicationEvent {

    private final transient UserInfoDto userAuthInfo;

    public UserAuthenticatedEvent(Object source, UserInfoDto userAuthInfo) {
        super(source);
        this.userAuthInfo = userAuthInfo;
    }
}