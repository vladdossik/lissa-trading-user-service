package lissa.trading.user.service.service.update.factory;

import lissa.trading.user.service.service.update.UpdateService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateServiceFactory {

    private final Map<SupportedBrokersEnum, UpdateService> UpdateServiceMap;

    public UpdateServiceFactory(List<UpdateService> UpdateServiceList) {
        UpdateServiceMap = UpdateServiceList
                .stream()
                .collect(Collectors.toMap(
                        UpdateService::getBroker,
                        service -> service));
    }

    public UpdateService getUpdateServiceByType(SupportedBrokersEnum supportedBroker) {
        return UpdateServiceMap.get(supportedBroker);
    }
}
