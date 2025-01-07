package lissa.trading.user.service.service.dataInitializer;

import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.repository.entity.FavoriteStocksEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class FavouriteStocksInitializerService implements DataInitializerService {

    private final UserRepository userRepository;
    private final FavoriteStocksEntityRepository stocksRepository;
    private final EasyRandom easyRandom;

    @Override
    public void createData() {
        if (stocksRepository.count() == 0) {
            for (User user : userRepository.findAll()) {
                FavoriteStocksEntity favoriteStocksEntity = easyRandom.nextObject(FavoriteStocksEntity.class);
                favoriteStocksEntity.setUser(user);
                stocksRepository.save(favoriteStocksEntity);
            }
            log.info("Favourite stocks successfully initialized");
        } else {
            log.info("Favourite stocks already initialized");
        }
    }
}
