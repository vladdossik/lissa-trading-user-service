package lissa.trading.user.service.service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationContext {
    private final static ThreadLocal<Boolean> externalSourceContext = ThreadLocal.withInitial(() -> false);

    public void setFromExternalSource(boolean isExternal) {
        externalSourceContext.set(isExternal);
    }

    public boolean isExternalSource() {
        return externalSourceContext.get();
    }

    public void clear() {
        externalSourceContext.set(false);
    }
}
