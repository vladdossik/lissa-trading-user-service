package lissa.trading.user.service.service.dataInitializer;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.UserAccountEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.UserAccountEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class UserAccountInitializerService implements DataInitializerService {

    private final UserRepository userRepository;
    private final UserAccountEntityRepository accountRepository;
    private final EasyRandom easyRandom;

    @Override
    public void createData() {
        if (accountRepository.count() == 0) {
            for (User user : userRepository.findAll()) {
                UserAccountEntity userAccountEntity = easyRandom.nextObject(UserAccountEntity.class);
                userAccountEntity.setUser(user);
                accountRepository.save(userAccountEntity);
            }
            log.info("User accounts successfully initialized");
        } else {
            log.info("User accounts already initialized");
        }
    }
}
