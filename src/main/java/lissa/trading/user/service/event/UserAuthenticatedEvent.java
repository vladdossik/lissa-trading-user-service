package lissa.trading.user.service.event;

import lissa.trading.user.service.dto.post.UserAuthInfoDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.annotation.Transient;

@Getter
public class UserAuthenticatedEvent extends ApplicationEvent {

    @Transient
    private final transient UserAuthInfoDto userAuthInfo;

    public UserAuthenticatedEvent(Object source, UserAuthInfoDto userAuthInfo) {
        super(source);
        this.userAuthInfo = userAuthInfo;
    }
}