package lissa.trading.user.service.dto.tinkoff.stock;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StocksPricesDto {
    private List<StockPrice> prices;
}
