package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.post.UserAuthInfoDto;
import lissa.trading.user.service.event.TempUserSavedEvent;
import lissa.trading.user.service.mapper.TempUserRegMapper;
import lissa.trading.user.service.model.TempUserReg;
import lissa.trading.user.service.repository.TempUserRegRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class TempUserRegServiceImpl implements TempUserRegService {

    private final TempUserRegRepository tempUserRegRepository;
    private final TempUserRegMapper tempUserRegMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createTempUser(UserAuthInfoDto userAuthInfo) {
        log.info("Creating temp user with info: {}", userAuthInfo);

        TempUserReg tempUserReg = tempUserRegMapper.toTempUserReg(userAuthInfo);
        TempUserReg savedTempUser = tempUserRegRepository.save(tempUserReg);
        log.info("Temp user saved: {}", savedTempUser);

        // Публикация кастомного события после успешного сохранения
        eventPublisher.publishEvent(new TempUserSavedEvent(this, savedTempUser));
    }
}