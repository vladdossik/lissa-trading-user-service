package lissa.trading.user.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lissa.trading.user.service.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class BalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "user_id", nullable = false),
            @JoinColumn(name = "telegram_nickname", nullable = false)
    })
    private User user;

    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @NotNull
    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @NotNull
    @Column(name = "total_amount_balance", nullable = false)
    private BigDecimal totalAmountBalance;
}