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
import java.time.OffsetDateTime;

@Entity
@Table(name = "operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class UserOperationsEntity {

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
    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @NotNull
    @Column(name = "operation_value", nullable = false)
    private BigDecimal operationValue;

    @NotNull
    @Column(name = "operation_date", nullable = false)
    private OffsetDateTime operationDate;
}