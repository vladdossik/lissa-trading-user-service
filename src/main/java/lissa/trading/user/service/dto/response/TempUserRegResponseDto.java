package lissa.trading.user.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempUserRegResponseDto {
    private UUID externalId;
    private String firstName;
    private String lastName;
    private String telegramNickname;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}