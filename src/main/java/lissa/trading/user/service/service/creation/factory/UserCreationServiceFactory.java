package lissa.trading.user.service.service.creation.factory;

import lissa.trading.user.service.service.creation.UserCreationService;

public interface UserCreationServiceFactory {
    UserCreationService getUserCreationServiceByType(SupportedBrokersEnum supportedBroker);
}
