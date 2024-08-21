package lissa.trading.user.service.service.user_creation;

import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreationServiceImpl implements UserCreationService {

    private final UserRepository userRepository;
    private final TempUserRegRepository tempUserRegRepository;
    private final UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createUserFromTempUserReg(TempUserReg tempUserReg) {
        try {
            log.info("Starting to create user from TempUserReg: {}", tempUserReg);

            User user = userMapper.toUserFromTempUserReg(tempUserReg);
            initializeDefaultValues(user);

            userRepository.save(user);
            log.info("User saved successfully: {}", user);

            tempUserRegRepository.delete(tempUserReg);
            log.info("TempUserReg deleted successfully: {}", tempUserReg);

        } catch (DataIntegrityViolationException e) {
            log.error("Error creating user from TempUserReg: {}", tempUserReg);
            throw new UserCreationException("Table error creating user from TempUserReg: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating User from TempUserReg: {}", tempUserReg, e);
            throw new UserCreationException("Error creating user from TempUserReg", e);
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
