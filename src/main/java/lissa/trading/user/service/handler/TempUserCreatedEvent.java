package lissa.trading.user.service.handler;

import lissa.trading.user.service.model.TempUserReg;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TempUserCreatedEvent {
    private final TempUserReg tempUserReg;
}
