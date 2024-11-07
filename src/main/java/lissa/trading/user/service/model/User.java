package lissa.trading.user.service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lissa.trading.user.service.model.entity.BalanceEntity;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.model.entity.MarginTradingMetricsEntity;
import lissa.trading.user.service.model.entity.PostsEntity;
import lissa.trading.user.service.model.entity.UserAccountEntity;
import lissa.trading.user.service.model.entity.UserOperationsEntity;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends UserReg {

    @NotNull
    @Column(name = "telegram_chat_id", unique = true)
    private Long telegramChatId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BalanceEntity> balances = new ArrayList<>();

    @Column(name = "percentage_change_since_yesterday")
    private BigDecimal percentageChangeSinceYesterday;

    @Column(name = "monetary_change_since_yesterday")
    private BigDecimal monetaryChangeSinceYesterday;

    @Column(name = "account_count")
    private Integer accountCount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserAccountEntity> userAccounts = new ArrayList<>();

    @Column(name = "is_margin_trading_enabled")
    private Boolean isMarginTradingEnabled = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MarginTradingMetricsEntity marginTradingMetrics;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FavoriteStocksEntity> favoriteStocks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserPositionsEntity> positions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserOperationsEntity> operations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PostsEntity> posts = new ArrayList<>();
}