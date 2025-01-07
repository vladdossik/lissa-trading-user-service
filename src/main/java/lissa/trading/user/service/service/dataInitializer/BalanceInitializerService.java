package lissa.trading.user.service.service.dataInitializer;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.BalanceEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.BalanceEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class BalanceInitializerService implements DataInitializerService {

    private final BalanceEntityRepository balanceEntityRepository;
    private final UserRepository userRepository;
    private final EasyRandom easyRandom;

    @Override
    public void createData() {
        if (balanceEntityRepository.count() == 0) {
            for (User user : userRepository.findAll()) {
                BalanceEntity balanceEntity = easyRandom.nextObject(BalanceEntity.class);
                balanceEntity.setUser(user);
                balanceEntityRepository.save(balanceEntity);
            }
            log.info("Balances successfully initialized");
        }
        log.info("Balances already initialized");
    }
}
