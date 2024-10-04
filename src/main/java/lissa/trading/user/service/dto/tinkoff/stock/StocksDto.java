package lissa.trading.user.service.dto.tinkoff.stock;

import lissa.trading.user.service.dto.tinkoff.Stock;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StocksDto {
    private List<Stock> stocks;
}
