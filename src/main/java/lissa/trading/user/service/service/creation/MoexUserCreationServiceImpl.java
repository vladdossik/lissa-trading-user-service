package lissa.trading.user.service.service.creation;

import lissa.trading.user.service.exception.OperationUnsupportedByBrokerException;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.mapper.UserMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.model.User;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import lissa.trading.user.service.service.creation.factory.SupportedBrokersEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoexUserCreationServiceImpl implements UserCreationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TempUserRegRepository tempUserRegRepository;
    private final static SupportedBrokersEnum broker = SupportedBrokersEnum.MOEX;

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = OperationUnsupportedByBrokerException.class)
    @Override
    public void createUserFromTempUserReg(TempUserReg tempUserReg) {
        try {
            log.info("Starting to create user from TempUserReg: {}", tempUserReg);
            User user = userMapper.toUserFromTempUserReg(tempUserReg);
            getTelegramInfo(user);
            log.info("Received information about the user's telegram:\n" +
                    "telegram chat id: {}", user.getTelegramChatId());

            userRepository.save(user);
            log.info("Saved user: {}", user);
            tempUserRegRepository.delete(tempUserReg);
            log.info("Deleted temp user: {}", tempUserReg);
            updateUser(user);
            log.info("User created and saved successfully: {}", user);
        }
        catch (OperationUnsupportedByBrokerException e) {
            throw e;
        }
        catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException while creating user from TempUserReg: {}", tempUserReg, e);
            throw new UserCreationException("Table error creating user from TempUserReg: " + e.getMessage());
        }
        catch (Exception e) {
            log.error("Exception while creating user from TempUserReg: {}", tempUserReg, e);
            throw new UserCreationException("Error creating user from TempUserReg", e);
        }
    }

    @Override
    public SupportedBrokersEnum getBroker() {
        return broker;
    }
}
