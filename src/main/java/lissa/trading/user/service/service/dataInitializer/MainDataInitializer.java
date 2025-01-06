package lissa.trading.user.service.service.dataInitializer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("local")
public class MainDataInitializer {

    private final List<DataInitializerService> dataInitializerServices;

    @PostConstruct
    public void init() {
        dataInitializerServices.forEach(DataInitializerService::createData);
    }
}
