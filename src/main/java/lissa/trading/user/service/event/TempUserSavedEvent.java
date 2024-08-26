package lissa.trading.user.service.event;

import lissa.trading.user.service.model.TempUserReg;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TempUserSavedEvent extends ApplicationEvent {

    private final transient TempUserReg tempUserReg;

    public TempUserSavedEvent(Object source, TempUserReg tempUserReg) {
        super(source);
        this.tempUserReg = tempUserReg;
    }
}
