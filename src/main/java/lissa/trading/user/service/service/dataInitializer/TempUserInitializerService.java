package lissa.trading.user.service.service.dataInitializer;

import lissa.trading.lissa.auth.lib.security.EncryptionService;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local")
public class TempUserInitializerService implements DataInitializerService {

    private final TempUserRegRepository tempUserRegRepository;
    private final EasyRandom easyRandom;

    @Override
    public void createData() {
        if (tempUserRegRepository.count() == 0) {
            for (int i = 0; i < 10; i++) {
                TempUserReg tempUserReg = easyRandom.nextObject(TempUserReg.class);
                tempUserReg.setTinkoffToken(generateTinkoffToken());
                tempUserRegRepository.save(tempUserReg);
            }
            log.info("Temp user successfully initialized");
        } else {
            log.info("Temp user already exists");
        }
    }

    private String generateTinkoffToken() {
        StringBuilder token = new StringBuilder("t.");
        for (int i = 0; i < 86; i++) {
            token.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(
                    (int) (Math.random() * 62)
            ));
        }
        return EncryptionService.encrypt(String.valueOf(token));
    }
}
