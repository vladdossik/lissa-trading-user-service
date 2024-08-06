package lissa.trading.user.service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.model.entity.PostsEntity;
import lissa.trading.user.service.model.entity.SubscriptionsEntity;
import lissa.trading.user.service.model.entity.UserOperationsEntity;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends UserReg {

    // TODO: переопределить toString (?) в связанных энтити, иначе выкидывается ошибка при попытке получить user

    @NotNull
    @Column(name = "telegram_chat_id", unique = true)
    private Long telegramChatId;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPositionsEntity> positions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOperationsEntity> operations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteStocksEntity> favoriteStocks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionsEntity> subscriptions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostsEntity> posts;
}