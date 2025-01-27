package lissa.trading.user.service.dto.notification;

import lissa.trading.user.service.dto.tinkoff.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationFavouriteStockDto {
    private String ticker;
    private String figi;
    private String serviceTicker;
    private String name;
    private String instrumentType;
    private Currency currency;
}
