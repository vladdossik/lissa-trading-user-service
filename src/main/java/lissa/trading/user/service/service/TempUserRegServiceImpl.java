package lissa.trading.user.service.service;

import jakarta.validation.Valid;
import lissa.trading.user.service.dto.post.TempUserRegPostDto;
import lissa.trading.user.service.dto.response.TempUserRegResponseDto;
import lissa.trading.user.service.handler.TempUserCreatedEvent;
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
    public TempUserRegResponseDto createTempUser(@Valid TempUserRegPostDto tempUserRegPostDto) {
        TempUserReg tempUserReg = tempUserRegMapper.toTempUserReg(tempUserRegPostDto);
        TempUserReg savedTempUser = tempUserRegRepository.save(tempUserReg);

        // Публикуем событие после успешного сохранения
        // TODO: получить данные от Tinkoff-API
        eventPublisher.publishEvent(new TempUserCreatedEvent(savedTempUser));

        return tempUserRegMapper.toTempUserRegResponseDto(savedTempUser);
    }
}