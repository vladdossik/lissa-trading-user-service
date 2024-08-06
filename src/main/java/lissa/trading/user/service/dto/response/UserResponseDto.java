package lissa.trading.user.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private UUID externalId;
    private String firstName;
    private String lastName;
    private Long telegramChatId;
    private String telegramNickname;
    private String tinkoffToken;
    private BigDecimal currentBalance;
    private BigDecimal percentageChangeSinceYesterday;
    private BigDecimal monetaryChangeSinceYesterday;
    private Integer accountCount;
    private Boolean isMarginTradingEnabled;
    private String marginTradingMetrics;
    private String tinkoffInvestmentTariff;
}