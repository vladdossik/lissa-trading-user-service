package lissa.trading.user.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDto {
    private Optional<String> firstName;
    private Optional<String> lastName;
    private Optional<String> telegramNickname;
    private Optional<String> tinkoffToken;
}