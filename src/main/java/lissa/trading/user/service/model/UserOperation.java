package lissa.trading.user.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserOperationsEntity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "operation_type")
    private String operationType;

    @NotNull
    @Column(name = "operation_value")
    private BigDecimal operationValue;

    @NotNull
    @Column(name = "operation_date")
    private LocalDateTime operationDate;
}