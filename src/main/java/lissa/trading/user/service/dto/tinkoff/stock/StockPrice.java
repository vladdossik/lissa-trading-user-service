package lissa.trading.user.service.dto.tinkoff.stock;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockPrice {
    private String figi;
    private Double price;
}
