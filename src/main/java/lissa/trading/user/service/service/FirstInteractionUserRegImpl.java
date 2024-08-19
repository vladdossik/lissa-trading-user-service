package lissa.trading.user.service.service;

import lissa.trading.user.service.dto.post.UserAuthInfoDto;
import lissa.trading.user.service.exception.UserCreationException;
import lissa.trading.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirstInteractionUserRegImpl implements FirstInteractionUserReg {

    private final TempUserRegService tempUserRegService;
    private final UserRepository userRepository;

    @Override
    public void createTempUser(UserAuthInfoDto userAuthInfoDto) {
        userRepository.findByTelegramNickname(userAuthInfoDto.getTelegramNickname()).ifPresent(user -> {
            throw new UserCreationException("User with telegram nickname " + userAuthInfoDto.getTelegramNickname() + " already exists!");
        });

        tempUserRegService.createTempUser(userAuthInfoDto);
    }
}
