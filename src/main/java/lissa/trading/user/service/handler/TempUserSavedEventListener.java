package lissa.trading.user.service.handler;

import lissa.trading.user.service.event.TempUserSavedEvent;
import lissa.trading.user.service.exception.OperationUnsupportedByBrokerException;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.service.creation.UserCreationService;
import lissa.trading.user.service.service.creation.factory.UserCreationServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempUserSavedEventListener {

    private final UserCreationServiceFactory userCreationServiceFactory;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTempUserCreatedEvent(TempUserSavedEvent event) {
        TempUserReg tempUserReg = event.getTempUserReg();
        UserCreationService userCreationService = userCreationServiceFactory
                .getUserCreationServiceByType(tempUserReg.getBroker());
        try {
            userCreationService.createUserFromTempUserReg(tempUserReg);
        }
        catch (OperationUnsupportedByBrokerException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Error creating user from temp user: {}", e.getMessage());
        }
    }
}