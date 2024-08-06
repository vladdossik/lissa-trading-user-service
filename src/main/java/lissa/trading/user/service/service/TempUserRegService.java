package lissa.trading.user.service.service;

import jakarta.validation.Valid;
import lissa.trading.user.service.dto.post.TempUserRegPostDto;
import lissa.trading.user.service.dto.response.TempUserRegResponseDto;

public interface TempUserRegService {
    TempUserRegResponseDto createTempUser(@Valid TempUserRegPostDto tempUserRegPostDto);
}
