package lissa.trading.user.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPatchDto {
    private Optional<String> firstName = Optional.empty();
    private Optional<String> lastName = Optional.empty();
    private Optional<String> telegramNickname = Optional.empty();
    private Optional<String> tinkoffToken = Optional.empty();
}