package lissa.trading.user.service.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPatchDto {
    @Builder.Default
    private Optional<String> firstName = Optional.empty();
    @Builder.Default
    private Optional<String> lastName = Optional.empty();
    @Builder.Default
    private Optional<Long> telegramChatId = Optional.empty();
    @Builder.Default
    private Optional<String> telegramNickname = Optional.empty();
    @Builder.Default
    private Optional<String> tinkoffToken = Optional.empty();
    @Builder.Default
    private Optional<BigDecimal> currentBalance = Optional.empty();
    @Builder.Default
    private Optional<BigDecimal> percentageChangeSinceYesterday = Optional.empty();
    @Builder.Default
    private Optional<BigDecimal> monetaryChangeSinceYesterday = Optional.empty();
    @Builder.Default
    private Optional<Integer> accountCount = Optional.empty();
    @Builder.Default
    private Optional<Boolean> isMarginTradingEnabled = Optional.empty();
    @Builder.Default
    private Optional<String> marginTradingMetrics = Optional.empty();
    @Builder.Default
    private Optional<String> tinkoffInvestmentTariff = Optional.empty();
}