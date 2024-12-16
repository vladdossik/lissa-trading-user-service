package lissa.trading.user.service.service.creation.factory;

import lissa.trading.user.service.service.creation.UserCreationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserCreationServiceFactoryImpl implements UserCreationServiceFactory {

    private final Map<SupportedBrokersEnum, UserCreationService> userCreationServiceMap;

    public UserCreationServiceFactoryImpl(List<UserCreationService> userCreationServiceList) {
        userCreationServiceMap = userCreationServiceList
                .stream()
                .collect(Collectors.toMap(
                        UserCreationService::getBroker,
                        service -> service));
    }

    public UserCreationService getUserCreationServiceByType(SupportedBrokersEnum supportedBroker) {
        return userCreationServiceMap.get(supportedBroker);
    }
}
