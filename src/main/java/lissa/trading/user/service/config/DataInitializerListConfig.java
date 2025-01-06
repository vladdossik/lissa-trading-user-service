package lissa.trading.user.service.config;

import lissa.trading.user.service.service.dataInitializer.BalanceInitializerService;
import lissa.trading.user.service.service.dataInitializer.DailyStatsInitializerService;
import lissa.trading.user.service.service.dataInitializer.DataInitializerService;
import lissa.trading.user.service.service.dataInitializer.FavouriteStocksInitializerService;
import lissa.trading.user.service.service.dataInitializer.OperationsInitializerService;
import lissa.trading.user.service.service.dataInitializer.PositionsInitializerService;
import lissa.trading.user.service.service.dataInitializer.PostsInitializerService;
import lissa.trading.user.service.service.dataInitializer.TempUserInitializerService;
import lissa.trading.user.service.service.dataInitializer.UserAccountInitializerService;
import lissa.trading.user.service.service.dataInitializer.UserInitializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Profile("local")
public class DataInitializerListConfig {

    private final UserInitializerService userInitializerService;
    private final BalanceInitializerService balanceInitializerService;
    private final FavouriteStocksInitializerService favouriteStocksInitializerService;
    private final PostsInitializerService postsInitializerService;
    private final UserAccountInitializerService userAccountInitializerService;
    private final OperationsInitializerService operationsInitializerService;
    private final PositionsInitializerService positionsInitializerService;
    private final DailyStatsInitializerService dailyStatsInitializerService;
    private final TempUserInitializerService tempUserInitializerService;

    @Bean
    public List<DataInitializerService> dataInitializerServices() {
        return List.of(userInitializerService,
                balanceInitializerService,
                favouriteStocksInitializerService,
                postsInitializerService,
                userAccountInitializerService,
                operationsInitializerService,
                positionsInitializerService,
                dailyStatsInitializerService,
                tempUserInitializerService);
    }
}
