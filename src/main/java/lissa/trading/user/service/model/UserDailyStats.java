package lissa.trading.user.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_daily_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private OffsetDateTime date;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "monetary_change")
    private BigDecimal monetaryChange;

    @Column(name = "percentage_change")
    private BigDecimal percentageChange;
}