package lissa.trading.user.service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lissa.trading.user.service.dto.tinkoff.Currency;
import lissa.trading.user.service.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "favorite_stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class FavoriteStocksEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "user_id", nullable = false),
            @JoinColumn(name = "telegram_nickname", nullable = false)
    })
    private User user;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "figi")
    private String figi;

    @Column(name = "service_ticker")
    private String serviceTicker;

    @Column(name = "name")
    private String name;

    @Column(name = "instrument_type")
    private String instrumentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;
}