package lissa.trading.user.service.handler;

import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.service.UserCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempUserCreatedEventListener {

    private final UserCreationService userCreationService;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleTempUserCreatedEvent(TempUserCreatedEvent event) {
        TempUserReg tempUserReg = event.getTempUserReg();
        try {
            userCreationService.createUserFromTempUserReg(tempUserReg);
        } catch (Exception e) {
            log.error("Error creating user from temp user: {}", e.getMessage());
        }
    }
}