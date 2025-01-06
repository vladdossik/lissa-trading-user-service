package lissa.trading.user.service.service.dataInitializer;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.UserOperationsEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.UserOperationsEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class OperationsInitializerService implements DataInitializerService {

    private final UserRepository userRepository;
    private final UserOperationsEntityRepository operationsEntityRepository;
    private final EasyRandom easyRandom;

    @Override
    public void createData() {
        if (operationsEntityRepository.count() == 0) {
            for (User user : userRepository.findAll()) {
                UserOperationsEntity operationsEntity = easyRandom.nextObject(UserOperationsEntity.class);
                operationsEntity.setUser(user);
                operationsEntityRepository.save(operationsEntity);
            }
            log.info("Operations successfully initialized");
        } else {
            log.info("Operations already initialized");
        }
    }
}
