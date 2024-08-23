package lissa.trading.user.service.service.creation;

import lissa.trading.user.service.dto.post.UserInfoDto;
import lissa.trading.user.service.event.TempUserSavedEvent;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.mapper.TempUserRegMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempUserCreationServiceImpl implements TempUserCreationService {

    private final UserRepository userRepository;
    private final TempUserRegRepository tempUserRegRepository;
    private final TempUserRegMapper tempUserRegMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createTempUser(UserInfoDto userInfoDto) {
        userRepository.findByTelegramNickname(userInfoDto.getTelegramNickname()).ifPresent(user -> {
            throw new UserCreationException("User with telegram nickname " + userInfoDto.getTelegramNickname() + " already exists!");
        });

        TempUserReg tempUserReg = tempUserRegMapper.toTempUserReg(userInfoDto);
        TempUserReg savedTempUser = tempUserRegRepository.save(tempUserReg);

        eventPublisher.publishEvent(new TempUserSavedEvent(this, savedTempUser));
    }
}
