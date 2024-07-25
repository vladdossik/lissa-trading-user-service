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
    @Column(name = "external_id", unique = true)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "telegram_chat_id", unique = true)
    private Long telegramChatId;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "telegram_nickname", unique = true)
    private String telegramNickname;

    @NotNull
    @Column(name = "tinkoff_token")
    private String tinkoffToken;

    @NotNull
    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    @NotNull
    @Column(name = "percentage_change_since_yesterday")
    private BigDecimal percentageChangeSinceYesterday;

    @NotNull
    @Column(name = "monetary_change_since_yesterday")
    private BigDecimal monetaryChangeSinceYesterday;

    @NotNull
    @Column(name = "account_count")
    private Integer accountCount;

    @NotNull
    @Column(name = "is_margin_trading_enabled")
    private Boolean isMarginTradingEnabled;

    @NotNull
    @Column(name = "margin_trading_metrics")
    private String marginTradingMetrics;

    @NotNull
    @Column(name = "tinkoff_investment_tariff")
    private String tinkoffInvestmentTariff;
}
