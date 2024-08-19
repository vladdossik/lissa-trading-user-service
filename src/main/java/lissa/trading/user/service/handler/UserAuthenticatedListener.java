package lissa.trading.user.service.handler;

import lissa.trading.user.service.event.UserAuthenticatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthenticatedListener {

    @EventListener
    public void handleUserAuthenticatedEvent(UserAuthenticatedEvent event) {
        log.info("User authenticated event received: {}", event.getUserAuthInfo());
    }
}