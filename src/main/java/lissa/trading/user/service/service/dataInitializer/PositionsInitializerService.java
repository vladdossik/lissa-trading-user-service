package lissa.trading.user.service.service.dataInitializer;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.UserPositionsEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class PositionsInitializerService implements DataInitializerService {

    private final UserRepository userRepository;
    private final UserPositionsEntityRepository positionsEntityRepository;
    private final EasyRandom easyRandom;

    @Override
    public void createData() {
        if (positionsEntityRepository.count() == 0) {
            for (User user : userRepository.findAll()) {
                UserPositionsEntity positionsEntity = easyRandom.nextObject(UserPositionsEntity.class);
                positionsEntity.setUser(user);
                positionsEntityRepository.save(positionsEntity);
            }
            log.info("Positions successfully initialized");
        } else {
            log.info("Positions already initialized");
        }
    }
}
