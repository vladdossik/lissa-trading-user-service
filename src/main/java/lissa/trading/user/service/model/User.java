package lissa.trading.user.service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lissa.trading.user.service.model.entity.FavoriteStocksEntity;
import lissa.trading.user.service.model.entity.PostsEntity;
import lissa.trading.user.service.model.entity.SubscriptionsEntity;
import lissa.trading.user.service.model.entity.UserOperationsEntity;
import lissa.trading.user.service.model.entity.UserPositionsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "external_id", unique = true)
    private UUID externalId;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "first_name")
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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