package lissa.trading.user.service.dto.tinkoff.stock;

import lissa.trading.user.service.dto.tinkoff.Stock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StocksDto {
    private List<Stock> stocks;
}
