package lissa.trading.user.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String externalId;

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