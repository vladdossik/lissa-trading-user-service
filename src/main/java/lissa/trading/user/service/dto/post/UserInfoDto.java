package lissa.trading.user.service.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    @NotNull
    private UUID externalId;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String telegramNickname;
    @NotNull
    private String tinkoffToken;
    @NotNull
    private List<String> roles;
}
