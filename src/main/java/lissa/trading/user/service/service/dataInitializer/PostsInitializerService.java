package lissa.trading.user.service.service.dataInitializer;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.PostsEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.PostsEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class PostsInitializerService implements DataInitializerService {

    private final UserRepository userRepository;
    private final PostsEntityRepository postsEntityRepository;
    private final EasyRandom easyRandom;

    @Override
    public void createData() {
        if (postsEntityRepository.count() == 0) {
            for (User user : userRepository.findAll()) {
                PostsEntity postsEntity = easyRandom.nextObject(PostsEntity.class);
                postsEntity.setUser(user);
                postsEntityRepository.save(postsEntity);
            }
            log.info("Posts successfully initialized");
        } else {
            log.info("Posts already initialized");
        }
    }
}
