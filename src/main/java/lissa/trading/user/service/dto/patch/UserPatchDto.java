package lissa.trading.user.service.dto.patch;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDto {
    private String firstName;
    private String lastName;
    private String telegramNickname;
    private String tinkoffToken;

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<String> getTelegramNickname() {
        return Optional.ofNullable(telegramNickname);
    }

    public Optional<String> getTinkoffToken() {
        return Optional.ofNullable(tinkoffToken);
    }
}