package lissa.trading.user.service.service;

import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserCreationServiceImpl implements UserCreationService {

    private final UserRepository userRepository;
    private final TempUserRegRepository tempUserRegRepository;
    private final UserMapper userMapper;

    @Override
    public void createUserFromTempUserReg(TempUserReg tempUserReg) {
        try {
            User user = userMapper.toUserFromTempUserReg(tempUserReg);

            initializeDefaultValues(user);

            userRepository.save(user);
            log.info("User saved: {}", user);

            // Удаляем временного пользователя после создания основного
            tempUserRegRepository.delete(tempUserReg);

            log.info("TempUserReg deleted: {}", tempUserReg);
        } catch (Exception e) {
            log.error("Error creating user from temp user: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating user from temp user", e); // Откат транзакции
        }
    }

    private void initializeDefaultValues(User user) {
        // TODO: получение данных из Tinkoff-API
        // Временный пример:

        if (user.getTelegramChatId() == null) {
            user.setTelegramChatId(0L);
        }
        if (user.getCurrentBalance() == null) {
            user.setCurrentBalance(BigDecimal.ZERO);
        }
        if (user.getPercentageChangeSinceYesterday() == null) {
            user.setPercentageChangeSinceYesterday(BigDecimal.ZERO);
        }
        if (user.getMonetaryChangeSinceYesterday() == null) {
            user.setMonetaryChangeSinceYesterday(BigDecimal.ZERO);
        }
        if (user.getAccountCount() == null) {
            user.setAccountCount(0);
        }
        if (user.getIsMarginTradingEnabled() == null) {
            user.setIsMarginTradingEnabled(false);
        }
        if (user.getMarginTradingMetrics() == null) {
            user.setMarginTradingMetrics("");
        }
        if (user.getTinkoffInvestmentTariff() == null) {
            user.setTinkoffInvestmentTariff("");
        }

        if (user.getPositions() == null) {
            List<UserPositionsEntity> defaultPositions = new ArrayList<>();
            defaultPositions.add(new UserPositionsEntity(null, user, "Default Position", BigDecimal.valueOf(1000)));
            user.setPositions(defaultPositions);
        }
        if (user.getOperations() == null) {
            user.setOperations(Collections.emptyList());
        }
        if (user.getFavoriteStocks() == null) {
            user.setFavoriteStocks(Collections.emptyList());
        }
        if (user.getSubscriptions() == null) {
            user.setSubscriptions(Collections.emptyList());
        }
        if (user.getPosts() == null) {
            user.setPosts(Collections.emptyList());
        }
    }
}
