package lissa.trading.user.service.service.creation;

import lissa.trading.user.service.dto.tinkoff.account.TinkoffTokenDto;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.feign.TinkoffAccountClient;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.service.creation.factory.SupportedBrokersEnum;
import lissa.trading.user.service.service.creation.update.UpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class TinkoffUserCreationServiceImpl implements UserCreationService {

    private final UserRepository userRepository;
    private final TempUserRegRepository tempUserRegRepository;
    private final TinkoffAccountClient tinkoffAccountClient;
    private final UserMapper userMapper;
    private final UpdateService updateService;
    private final static SupportedBrokersEnum broker = SupportedBrokersEnum.TINKOFF;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createUserFromTempUserReg(TempUserReg tempUserReg) {
        try {
            log.info("Starting to create user from TempUserReg: {}", tempUserReg);
            User user = userMapper.toUserFromTempUserReg(tempUserReg);
            tinkoffAccountClient.setTinkoffToken(new TinkoffTokenDto(tempUserReg.getTinkoffToken()));
            getTelegramInfo(user);
            log.info("Received information about the user's telegram:\n" +
                    "telegram chat id: {}", user.getTelegramChatId());

            userRepository.save(user);
            log.info("Saved user: {}", user);
            tempUserRegRepository.delete(tempUserReg);
            log.info("Deleted temp user: {}", tempUserReg);
            updateUser(user);
            log.info("User created and saved successfully: {}", user);

        } catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException while creating user from TempUserReg: {}", tempUserReg, e);
            throw new UserCreationException("Table error creating user from TempUserReg: " + e.getMessage());
        } catch (Exception e) {
            log.error("Exception while creating user from TempUserReg: {}", tempUserReg, e);
            throw new UserCreationException("Error creating user from TempUserReg", e);
        }
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);

        user.setAccountCount(updateService.updateUserAccounts(user));
        String tinkoffAccountId = getTinkoffAccountId(user);

        updateService.updateUserBalance(user, tinkoffAccountId);

        updateService.updateUserMarginMetrics(user, tinkoffAccountId);
        log.info("User updated with margin metrics: {}", user);

        updateService.updateUserFavouriteStocks(user);
        log.info("User updated with favourite stocks: {}", user);

        updateService.updateUserPositions(user, tinkoffAccountId);
        log.info("User updated with positions: {}", user);
    }

    @Override
    public SupportedBrokersEnum getBroker() {
        return broker;
    }

    private String getTinkoffAccountId(User user) {
        try {
           return user.getUserAccounts().get(0).getAccountId();
        } catch (Exception e) {
            log.error("Failed to get Tinkoff account ID.", e);
            throw new UserCreationException("Error fetching Tinkoff account ID.", e);
        }
    }
}