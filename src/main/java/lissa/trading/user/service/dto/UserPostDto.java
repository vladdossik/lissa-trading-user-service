package lissa.trading.user.service.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPostDto {
    @NotNull
    @Size(min = 1, max = 50)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;

    @NotNull
    private Long telegramChatId;

    @NotNull
    @Size(min = 1, max = 50)
    private String telegramNickname;

    @NotNull
    private String tinkoffToken;

    @NotNull
    private BigDecimal currentBalance;

    @NotNull
    private BigDecimal percentageChangeSinceYesterday;

    @NotNull
    private BigDecimal monetaryChangeSinceYesterday;

    @NotNull
    private Integer accountCount;

    @NotNull
    private Boolean isMarginTradingEnabled;

    @NotNull
    private String marginTradingMetrics;

    @NotNull
    private String tinkoffInvestmentTariff;
}
